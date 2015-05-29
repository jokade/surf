// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest-servlet
// Description:
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest.servlet

import javax.servlet.AsyncContext
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import surf.{Request, ServiceRefFactory}
import surf.rest.RESTResponse._
import surf.rest.{RESTRequest, StaticRESTResource, RESTResource}
import surf.CompleterFactory.Implicits.globalCF
import surf.Directives._

import scala.util.{Try, Success, Failure}

abstract class RESTServlet extends HttpServlet {
  import RESTRequest._
  import surf.Directives._

  /**
   * Factory used to create ServiceRefS
   */
  implicit def handlerRef: ServiceRefFactory

  def root: RESTResource

  /**
   * Returns a Map of annotations to be added to the [[Request]], or None.
   *
   * @param req
   */
  def annotations(req: HttpServletRequest) : Option[Map[String,Any]] = None


  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    handleRequest(req,resp)(GETRequest(_,Map.empty[String,String]))

  override def doPut(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    handleRequest(req,resp)(PUTRequest(_,Map.empty[String,String]))

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    handleRequest(req,resp)(POSTRequest(_,Map.empty[String,String]))

  private def getResource(req: HttpServletRequest) = req.getPathInfo match {
    case null | "/" => Some(root)
    case path => root.child( path.split("/").tail )
  }

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

  private def handleRequest(req: HttpServletRequest, resp: HttpServletResponse)(f: RESTResource => Request) =
    getResource(req) match {
      case None =>
        resp.setStatus(404)
      case Some(r) => (annotations(req) match {
        case None => f(r)
        case Some(as) => f(r).withAnnotations( _ => as )
      }) >> r.handler onComplete handleResponse(resp,req.startAsync())
    }
}

