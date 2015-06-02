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

import scala.annotation.tailrec
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
        val result = Await.result(flow.future, timeout)
        handleResponse(resp)(result)
  }

  // TODO: do we need to return a PartialFunction here?
  private def handleResponse(resp: HttpServletResponse) : PartialFunction[Any,Any] = {
    case OK(data,ctype) =>
      resp.setStatus(200)
      resp.setContentType(ctype.toString)
      writeData(data,resp)
    case NoContent =>
      resp.setStatus(204)
    case BadRequest(msg) =>
      error(resp,400,msg)
    case NotFound =>
      resp.setStatus(404)
    case Conflict(msg) =>
      error(resp,409,msg)
    case Error(msg) =>
      error(resp,500,msg)
    case MethodNotAllowed =>
      error(resp,405,"")
    case Failure(ex) =>
      error(resp,500,ex.toString)
    case Success(x) =>
      handleResponse(resp).apply(x)
    case x =>
      error(resp,500,"Unknonw REST response of type "+x.getClass)
  }

  // TODO: use streams (and possible callbacks?)
  private def writeData(data: Any, resp: HttpServletResponse) : Unit = {
    val w = resp.getWriter
    val d = data.toString
    w.print(d)
    resp.setContentLength(d.length)
  }

  private def error(resp: HttpServletResponse, status: Int, msg: String): Unit = {
    resp.sendError(status,msg)
  }

}
