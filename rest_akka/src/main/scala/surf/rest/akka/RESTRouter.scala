// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest
// Description: Factory for akka-http based routes for handling requests to RESTResources
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest.akka

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.stream.FlowMaterializer
import surf.rest.RESTResponse._
import surf.rest.{RESTRequest, RESTResource}
import surf.{Request, ServiceRefFactory}

import scala.Error
import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

/**
 * Factory for akka-http routes to handle requests to [[RESTResource]]S
 */
object RESTRouter {
  import RESTRequest._

  def apply(prefix: String, root: RESTResource)
           (implicit sf: ServiceRefFactory, ec: ExecutionContext, fm: FlowMaterializer) : server.Route = {

    pathPrefix(prefix) {
      pathEnd {
        parameterMap { params =>
          get { GETRequest(root, params) >> root.handler } ~
          delete { DELETERequest(root, params) >> root.handler } ~
          entity(as[String]) { body =>
            put { PUTRequest(root, params, body) >> root.handler } ~
            post { POSTRequest(root, params, body) >> root.handler }
          }
        }
      } ~
      path(Segments) { path =>
        val resourceOption = root.child(path)
        if (resourceOption.isEmpty) reject
        else {
          val resource = resourceOption.get
          val handler = resource.handler
          parameterMap { params =>
            get { GETRequest(resource, params) >> handler } ~
            delete { DELETERequest(resource, params) >> handler } ~
            entity(as[String]) { body =>
              put { PUTRequest(resource, params, body) >> handler } ~
              post { POSTRequest(resource, params, body) >> handler }
            }
          }
        }
      }
    }
  }

  implicit def handleRequest(req: Request)(implicit ec: ExecutionContext) : server.Route = {

    onSuccess(req.future) {
      case ok: OK => complete(ok)
      case NoContent => complete( HttpResponse(status = StatusCodes.NoContent) )
      case MethodNotAllowed => complete( HttpResponse(status = StatusCodes.MethodNotAllowed) )
      case NotFound => reject
      case BadRequest(msg) => complete( HttpResponse(status = StatusCodes.BadRequest, entity = msg) )
      case Error(msg) => complete( HttpResponse(status = StatusCodes.InternalServerError, entity = msg) )
      case res => failWith(new RuntimeException(s"REST response not supported"))
    }
  }

}
