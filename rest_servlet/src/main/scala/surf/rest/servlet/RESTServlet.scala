// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest_servlet
// Description:
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest.servlet

import java.util.function.BinaryOperator
import javax.servlet.AsyncContext
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

import surf.rest.RESTResponse._
import surf.rest.{RESTAction, RESTResolver}
import surf.{Annotations, Request}

import scala.collection.JavaConverters._
import scala.collection.immutable.{AbstractMap, DefaultMap}
import scala.util.{Failure, Success, Try}

abstract class RESTServlet extends RESTServlet.Base {

  private def error(http: HttpServletResponse, async: AsyncContext, status: Int, msg: String): Unit = {
    http.setContentType("text/plain")
    http.setStatus(status)
    val w = async.getResponse.getWriter
    try {
      w.write(msg)
    } finally {
      w.close()
    }
    async.complete()
  }

  private def handleResponse(http: HttpServletResponse, async: AsyncContext) : PartialFunction[Try[Any],Unit] = {
    case Failure(r) =>
      error(http,async,500,r.toString)
    case Success(OK(data,ctype)) =>
      http.setStatus(200)
      http.setContentType(ctype.toString)
      val w = async.getResponse.getWriter
      try{ w.write(data.toString) } finally {w.close()}
      async.complete()
    // TODO: CREATED
    //case Success(Created(data)) =>
    case Success(NoContent) =>
      http.setStatus(204)
      async.complete()
    case Success(BadRequest(msg)) =>
      http.setStatus(400)
      async.complete()
    case Success(NotFound) =>
      http.setStatus(404)
      async.complete()
    case Success(Conflict(msg)) =>
      error(http,async,409,msg)
    case Success(Error(msg)) =>
      error(http,async,500,msg)
    case Success(MethodNotAllowed) =>
      http.setStatus(405)
      async.complete()
    case Success(x) =>
      error(http,async,500,"Unknown REST response of type "+x.getClass)
  }

//    getResource(req) match {
//      case None =>
//        resp.setStatus(404)
//      case Some(r) => (annotations(req) match {
//        case None => f(r)
//        case Some(as) => f(r).withAnnotations( _ => as )
//      }) >> r.handler onComplete handleResponse(resp,req.startAsync())
//    }
  override def handleRequest(action: RESTAction, req: HttpServletRequest, resp: HttpServletResponse): Unit = ???
}

object RESTServlet {
  abstract class Base extends HttpServlet {
    import surf.rest.{Params, Path}


    /**
     * The resolver used to find a handler service for each request
     */
    def resolver: RESTResolver

    /**
     * Returns a Map of annotations to be added to the [[Request]], or None.
     *
     * @param req
     */
    def annotations(req: HttpServletRequest) : Annotations = Map()

    /**
     * Returns a map with all parameters set on the specified request.
     */
    final def params(req: HttpServletRequest) : Params = new ParamsMap(req.getParameterMap)

    @inline
    final def path(req: HttpServletRequest) : Path = Path(req.getPathInfo)

    @inline
    final def body(req: HttpServletRequest) : String = {
      val len = req.getContentLength
      req.getReader.lines().reduce(new BinaryOperator[String] {
        override def apply(t: String, u: String): String = t ++ u
      }).orElse("")
    }

    final override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit =
      handleRequest(RESTAction.GET(path(req),params(req)),req,resp)

    final override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit =
      handleRequest(RESTAction.PUT(path(req),params(req),body(req)),req,resp)

    final override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit =
      handleRequest(RESTAction.POST(path(req),params(req),body(req)),req,resp)

    final override def doDelete(req: HttpServletRequest, resp: HttpServletResponse): Unit =
      handleRequest(RESTAction.DELETE(path(req),params(req)),req,resp)

    def handleRequest(action: RESTAction, req: HttpServletRequest, resp: HttpServletResponse) : Unit



    // TODO: more efficient solution?
    private class ParamsMap(m: java.util.Map[String,Array[String]])
      extends AbstractMap[String,Array[String]] with DefaultMap[String,Array[String]] {
      override def get(key: String): Option[Array[String]] = if(m.containsKey(key)) Some(m.get(key)) else None

      override def iterator: Iterator[(String, Array[String])] = m.asScala.toIterator
    }
  }

  class HttpServletResponseWriter(resp: HttpServletResponse) extends ResponseWriter {
    override def write(s: String): Unit = resp.getWriter.write(s)
  }
  object HttpServletResponseWriter {
    def apply(resp: HttpServletResponse): ResponseWriter = new HttpServletResponseWriter(resp)
  }
}
