// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest-servlet
// Description: HttpServlet that handles all request synchronously (blocking) on the calling thread.
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest.servlet

import java.io.InputStream
import java.nio.file.{FileSystems, Files}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import surf.rest.{RESTAction, RESTRequest, RESTResolver}
import surf.rest.RESTResponse._

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
    case RespondWithStream(stream,ctype,status) =>
      resp.setStatus(status)
      resp.setContentType(ctype)
      writeStream(stream,resp)
    case r: RespondWithResource =>
      respondWithResource(resp,r)
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
      error(resp,500,"Unknown REST response of type "+x.getClass)
  }


  private def error(resp: HttpServletResponse, status: Int, msg: String): Unit = {
    resp.sendError(status,msg)
  }

  private def writeBody(write: ResponseWriter, resp: HttpServletResponse): Unit = write match {
    case Left(w) => w( new ServletResponseWriter(resp) )
    case Right(w) => w( resp.getOutputStream)
  }

  private def writeStream(in: InputStream, resp: HttpServletResponse): Unit = {
    val buffer = new Array[Byte](1024)
    val out = resp.getOutputStream
    var len = in.read(buffer)
    while (len != -1) {
      out.write(buffer, 0, len)
      len = in.read(buffer)
    }
    in.close()
    out.close()
  }

  private def respondWithResource(resp: HttpServletResponse, r: RespondWithResource): Unit = {
    val p = FileSystems.getDefault.getPath(r.path)
    if(Files.isReadable(p)) {
      logger.debug("responding with resource {} (Content-Type: {})",p,r.ctype)
      val out = resp.getOutputStream
      resp.addHeader("Content-Type", r.ctype)
      resp.setStatus(r.status)
      Files.copy(p,out)
      out.close()
    } else {
      logger.warn("cannot respond with resource '{}': file is not readable",p)
      error(resp,404,"Error 404 - Not Found")
      //        error(req,500,"Error 500 - Internal Server Error")
    }
  }

  private class ServletResponseWriter(resp: HttpServletResponse) extends StringWriter {
    override def write(s: String, charset: String): Unit = resp.getWriter.write(s)
  }
}
