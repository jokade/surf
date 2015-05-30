// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest-servlet
// Description: HttpServlet that handles all request synchronously via a surf RESTService
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest.servlet

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import surf.Request
import surf.rest.RESTResource
import surf.rest.RESTResponse._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.{Success, Failure, Try}

abstract class SyncRESTServlet extends RESTServlet.Base {
  private val timeout = Duration(5,"seconds")

  override protected def handleRequest(req: HttpServletRequest, resp: HttpServletResponse)(f: (RESTResource) => Request): Unit =
    getResource(req) match {
      case None =>
        resp.setStatus(404)
      case Some(r) =>
        val flow = (annotations(req) match {
          case None => f(r)
          case Some(as) => f(r).withAnnotations( _ => as )
        }) >> r.handler
        Await.ready( flow.onComplete(handleResponse(resp)).future, timeout )
  }

  private def handleResponse(resp: HttpServletResponse) : PartialFunction[Try[Any],Any] = {
    case Failure(r) =>
      error(resp,500,"Internal error in REST service layer")
    case Success(OK(data,ctype)) =>
      resp.setStatus(200)
      resp.setContentType(ctype.toString)
      writeData(data,resp)
    case Success(NoContent) =>
      resp.setStatus(204)
    case Success(BadRequest(msg)) =>
      resp.setStatus(400)
    case Success(NotFound) =>
      resp.setStatus(404)
    case Success(Conflict(msg)) =>
      error(resp,409,msg)
    case Success(Error(msg)) =>
      error(resp,500,msg)
    case Success(MethodNotAllowed) =>
      error(resp,405,"")
    case Success(x) =>
      error(resp,500,"Unknonw REST response of type "+x.getClass)
  }

  // TODO: use streams (and possible callbacks?)
  private def writeData(data: Any, resp: HttpServletResponse) : Unit = {
    val w = resp.getWriter
    try{ w.print(data.toString) }
    finally{ w.close() }
  }

  private def error(resp: HttpServletResponse, status: Int, msg: String): Unit = {
    resp.setContentType("text/plain")
    resp.setStatus(status)
    val w = resp.getWriter
    try {
      w.write(msg)
    } finally {
      w.close()
    }
  }

}
