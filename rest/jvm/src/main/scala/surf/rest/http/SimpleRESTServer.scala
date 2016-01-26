//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / jvm
// Description: A RESTful HTTP server based on com.sun.net.httpserver

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest.http

import java.io.{OutputStream, PrintWriter, IOException}
import java.net.InetSocketAddress
import java.nio.file.{FileSystems, Files}

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}
import slogging.LazyLogging
import surf.dsl._
import surf.rest.RESTResponse._
import surf.rest._
import surf.rest.http.SimpleRESTServer.{Config, SimpleRESTHandler}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

class SimpleRESTServer(config: Config, resolver: RESTResolver) extends LazyLogging {
  import config._

  val _http = HttpServer.create(new InetSocketAddress(port),0)
  _http.createContext(path, new SimpleRESTHandler(config,resolver))

  def start() : SimpleRESTServer = {
    logger.info(s"Starting HTTP server on port $port with context path '$path")
    _http.start()
    logger.info("    ready")
    this
  }

  def stop(delay: Int = 0): Unit = {
    logger.info(s"Stopping HTTP server on port $port with context path '$path'")
    _http.stop(delay)
    logger.info("    done")
  }

  def waitForEnter(delay: Int = 0): Unit = {
    println("Hit ENTER to stop the server")
    Console.in.readLine()
    stop(delay)
  }
}

object SimpleRESTServer {
  case class Config(port: Int, path: String, timeout: Duration, executionContext: ExecutionContext)

  class SimpleRESTHandler(config: Config, resolver: RESTResolver) extends HttpHandler with LazyLogging {
    import config.timeout
    implicit val ec = config.executionContext

    override def handle(req: HttpExchange): Unit = {
      resolver.resolveRESTService(getRESTAction(req)) match {
        case None =>
          error(req,404,"Not found")
        case Some((service,act)) =>
          val res = await{ RESTRequest(act) >> service }(timeout)
          handleResult(res, req)
      }
    }

    private def getRESTAction(req: HttpExchange) : RESTAction = {
      val path = getPath(req)
      val params = getParams(req)
      val act = req.getRequestMethod match {
        case "GET" => RESTAction.GET(path,params)
        case x => throw new ServerException(405,s"Method '$x' not supported")
      }
      logger.debug(s"handling HTTP request $act (port: ${config.port}, context: ${config.path})")
      act
    }

    private def error(req: HttpExchange, code: Int, msg: String = "") = {
      logger.debug(s"  response error: $code $msg")
      val bytes = msg.getBytes("iso-8859-1")
      req.sendResponseHeaders(code,bytes.length)
      val out = req.getResponseBody
      out.write(bytes)
      out.close()
    }

    @annotation.tailrec
    private def handleResult(result: Any, req: HttpExchange) : Unit = result match {
      case Success(x) =>
        handleResult(x,req)
      case Failure(ex) =>
        logger.error(s"internal error while processing request ${req.getRequestMethod} ${req.getRequestURI}",ex)
        error(req,500,"Error 500 - Internal Server Error")
      case OK(write,ctype) =>
        writeResponse(200,ctype,write,req)
      case NoContent =>
        logger.debug("  response: 204 No Content")
        req.sendResponseHeaders(204,-1)
      case NotFound =>
        error(req,404,"Error 404 - Not Found")
      case r: RespondWithResource =>
        respondWithResource(req,r)
      case x =>
        logger.error(s"unknown REST response of type ${x.getClass} (request ${req.getRequestMethod} ${req.getRequestURI})")
        error(req,500,"Error 500 - Internal Server Error")
    }

    @inline
    private def getPath(req: HttpExchange): Path = Path( req.getRequestURI.getPath )

    @inline
    private def getParams(req: HttpExchange): Params = Map()

    private def writeResponse(code: Int, ctype: String, writer: RESTResponse.ResponseWriter, req: HttpExchange) =
      writer match {
        case Left(w) =>  w( new StringResponseWriter(code,ctype,req) )
        case Right(w) =>
          ???
          //w( req.getResponseBody )
      }

    private def respondWithResource(req: HttpExchange, r: RespondWithResource): Unit = {
      import r._
      val p = FileSystems.getDefault.getPath(path)
      if(Files.isReadable(p)) {
        val out = req.getResponseBody
        req.getResponseHeaders.add("Content-Type",ctype)
        req.sendResponseHeaders(status,0)
        Files.copy(p,out)
        out.close()
      } else {
        logger.error(s"error while processing $r: file '$p' is not readable")
        error(req,500,"Error 500 - Internal Server Error")
      }
    }

    case class ServerException(code: Int, msg: String) extends IOException(msg)

    private class StringResponseWriter(code: Int, ctype: String, req: HttpExchange) extends StringWriter {
      override def write(s: String, charset: String): Unit = {
        val bytes = s.getBytes(charset)
        req.getResponseHeaders.add("Content-Type",s"$ctype; charset=$charset")
        req.sendResponseHeaders(code,bytes.length)
        val out = req.getResponseBody
        out.write(bytes)
        out.close()
      }
    }


  }
}