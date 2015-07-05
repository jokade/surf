// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest
// Description: Factory for akka-http based routes for handling requests to RESTResources
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.akka.rest

import akka.http.scaladsl.model.{StatusCodes, HttpResponse}
import akka.stream.FlowMaterializer

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions
import akka.http.scaladsl.server
import server.Directives._
import surf.{ServiceRefFactory, CompleterFactory, Request}
import surf.rest.{GET, RESTRequest, RESTResource}

/**
 * Factory for akka-http routes to handle requests to [[RESTResource]]S
 */
object RESTRouter {

  def apply(prefix: String, root: RESTResource)
           (implicit cf: CompleterFactory, sf: ServiceRefFactory, ec: ExecutionContext, fm: FlowMaterializer) : server.Route = {
    import RESTRequest._

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
    import surf.rest.RESTResponse._
    import RESTResponseMarshaller._

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
