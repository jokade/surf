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

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit =
    handleRequest(req,resp)(GETRequest(_,Map.empty[String,String]))


  private def getResource(req: HttpServletRequest) = req.getPathInfo match {
    case null | "/" => Some(root)
    case path => root.child( path.split("/").tail )
  }

  private def handleResponse(http: HttpServletResponse, async: AsyncContext) : PartialFunction[Try[Any],Unit] = {
    case Failure(_) =>
      http.setStatus(500)
      async.complete()
    case Success(OK(data,ctype)) =>
      http.setStatus(200)
      http.setContentType(ctype.toString)
      val w = async.getResponse.getWriter
      w.write(data.toString)
      w.close()
      async.complete()
    case Success(NoContent) =>
      http.setStatus(204)
      async.complete()
    case Success(BadRequest(msg)) =>
      http.setStatus(400)
      async.complete()
    case Success(NotFound) =>
      http.setStatus(404)
      async.complete()
    case Success(Error(msg)) =>
      http.setStatus(500)
      async.complete()
    case Success(MethodNotAllowed) =>
      http.setStatus(405)
      async.complete()
    case x =>
      http.setStatus(500)
      async.complete()
  }

  private def handleRequest(req: HttpServletRequest, resp: HttpServletResponse)(f: RESTResource => Request) =
    getResource(req) match {
      case None =>
        resp.setStatus(404)
      case Some(r) =>
        f(r) >> r.handler onComplete handleResponse(resp,req.startAsync())
    }
}

