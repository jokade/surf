//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / nodejs
// Description: Provides a simple HTTP/REST server for Node.js.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest.nodejs

import nodejs.http.{IncomingMessage, ServerResponse}
import nodejs._
import slogging.LazyLogging
import surf.rest.RESTResponse._
import surf.{ServiceProps, ServiceRefFactory, ServiceRef}
import surf.dsl._
import surf.rest.RESTAction.{DELETE, GET, POST, PUT}
import surf.rest.{RESTAction, RESTService}

import scala.concurrent.ExecutionContext
import scala.scalajs.js

class SimpleRESTServer(port: Int, root: ServiceRef)(implicit ec: ExecutionContext) extends LazyLogging {
  private lazy val _server = Http.createServer(receive _)
  _server.listen(port.toShort)
  logger.info(s"Started HTTP server on port $port")

  def shutdown(after: ()=>Unit = null): Unit = {
    _server.close(after)
  }

  private def receive(req: IncomingMessage, res: ServerResponse): Unit = {
    val url = URL().parse(req.url,true)
    val pathname = url("pathname")
    val path =
      if(pathname.startsWith("/")) pathname.split("/").tail
      else pathname.split("/")

    val params = url("query").asInstanceOf[js.Dictionary[String]].toMap
    val action = req.method match {
      case "GET"    => GET(path,params)
      case "POST"   => POST(path,params,null)
      case "PUT"    => PUT(path,params,null)
      case "DELETE" => DELETE(path,params)
      case _        =>
        logger.error(s"Received HTTP request with unsupported method: '${req.method}'")
        res.writeHead(405)
        res.end()
        return
    }
    logger.trace(s"Received HTTP request: $action")

    action >> root onSuccess {
      case OK(write,ctype) =>
        res.writeHead(200,js.Dictionary("Content-Type"->ctype))
        writeBody(write,res)
        res.end()
      case RespondWithResource(file,ctype,status) =>
        sendResource(action,file,ctype,status,res)
      case NoContent =>
        res.writeHead(204)
        res.end()
      case BadRequest(msg) =>
        res.writeHead(400)
        res.end(msg)
      case NotFound =>
        res.writeHead(404)
        res.end()
      case MethodNotAllowed =>
        res.writeHead(405)
        res.end()
      case Error(msg) =>
        logger.error(s"error while processing request $action: $msg")
        res.writeHead(500)
        res.end()
      case x =>
        logger.error(s"invalid response: $x")
        res.writeHead(500)
        res.end()
    } onFailure {
      case ex =>
        logger.error(s"error while processing request $action: $ex")
        res.writeHead(500)
        res.end()
    }
  }

  private def writeBody(write: ResponseWriter, res: ServerResponse) : Unit = write match {
    case Left(w) => w(new StringResponseWriter(res))
    case Right(w) => ???
  }

  private def sendResource(action: RESTAction, file: String, ctype: String, status: Int, res: ServerResponse) : Unit =
    FS().readFile(file, (err:NodeError, data:js.UndefOr[NodeBuffer]) =>{
      if(err!=null) {
        logger.debug(s"Could not respond with resource '$file': ${err.message}")
        res.writeHead(404)
        res.end()
      }
      else {
        res.writeHead(status,js.Dictionary("Content-Type"->ctype))
        res.end(data.get)
      }
    })


  private class StringResponseWriter(res: ServerResponse) extends StringWriter {
    override def write(s: String, charset: String): Unit = res.write(s)
  }
}


object SimpleRESTServer {

//  def apply(port: Short)(implicit ec: ExecutionContext) : SimpleRESTServer = apply(port,null)

  def apply(port: Short, root: ServiceRef)(implicit ec: ExecutionContext) : SimpleRESTServer = new SimpleRESTServer(port,root)

  def apply(port: Short, root: =>RESTService)(implicit ec: ExecutionContext, factory: ServiceRefFactory) : SimpleRESTServer =
    new SimpleRESTServer(port, factory.serviceOf(ServiceProps(root)))

}
