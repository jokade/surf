// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest-servlet
// Description: HttpServlet that handles all request synchronously (blocking) on the calling thread.
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest.servlet

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import surf.rest.RESTResponse._
import surf.rest.{RESTAction, RESTRequest, RESTResolver}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}
import scala.util.{Failure, Success}

class SyncRESTServlet(val resolver: RESTResolver, timeout: Duration)
                     (implicit ec: ExecutionContext) extends RESTServlet.Base {

  def postProcess(resp: HttpServletResponse): Unit = {}

  override def handleRequest(action: RESTAction, req: HttpServletRequest, resp: HttpServletResponse): Unit =
    resolver.resolveRESTService(action) match {
      case None =>
        resp.setStatus(404)
        postProcess(resp)
      case Some((service,act)) =>
        val result = Await.result( (RESTRequest(act,annotations(req)) >> service).future, timeout )
        handleResult(result,resp)
        postProcess(resp)
    }

  // TODO: do we need to return a PartialFunction here?
  @annotation.tailrec
  private def handleResult(result: Any, resp: HttpServletResponse) : Unit = result match {
    case OK(writeData,ctype) =>
      resp.setStatus(200)
      resp.setContentType(ctype)
      writeBody(writeData,resp)
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
      handleResult(x,resp)
    case x =>
      error(resp,500,"Unknonw REST response of type "+x.getClass)
  }


  private def error(resp: HttpServletResponse, status: Int, msg: String): Unit = {
    resp.sendError(status,msg)
  }

  private def writeBody(write: ResponseWriter, resp: HttpServletResponse): Unit = write match {
    case Left(w) => w( new ServletResponseWriter(resp) )
    case Right(w) => w( resp.getOutputStream)
  }

  private class ServletResponseWriter(resp: HttpServletResponse) extends StringWriter {
    override def write(s: String, charset: String): Unit = resp.getWriter.write(s)
  }
}
