// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest
// Description: Provides a simple HTTP server for handling REST request
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest.akka

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.{Http, server}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}
import surf.akka.ServiceActorRefFactory

import scala.concurrent.ExecutionContext
import scala.language.implicitConversions

/**
 * Starts a simple HTTP server handling the specified list of top-level RESTResources.
 *
 * @param system
 * @param interface
 * @param port
 */
class SimpleRESTServer(route: server.Route,
                       interface: String,
                       port: Int,
                       afterStop: ()=>Unit)
                      (implicit val system: ActorSystem,
                       implicit val cf: CompleterFactory,
                       implicit val materializer: FlowMaterializer) {

  import system.dispatcher

  //val binding = Http().bind(interface,port)
  val serverSource = Http().bind(interface,port)

  val binding = serverSource.to(Sink.foreach{ connection =>
    connection handleWith Route.handlerFlow(route)
  }).run()

  //val materializedMap = binding startHandlingWith Route.asyncHandler(route)

  def stop() = ??? //binding.value.get.get.unbind(materializedMap).onComplete( _ => afterStop() )
}

object SimpleRESTServer {
  def apply(rootResources: Iterable[RESTResource],
            interface: String = "127.0.0.1",
            port: Int = 8081,
            prefix: String = "rest")
           (implicit cf: CompleterFactory) : SimpleRESTServer = {

    implicit val system = ActorSystem("rest")
    implicit val materializer = ActorFlowMaterializer()

    //implicit val materializer = FlowMaterializer()
    implicit val serviceRefFactory = ServiceActorRefFactory(system)

    val root = StaticRESTResource("rest",rootResources)

    new SimpleRESTServer( RESTRouter(prefix,root) ,interface,port, () => system.shutdown())(system,cf,materializer)
  }

  def fromRoute(interface: String = "127.0.0.1",
                port: Int = 8081,
                actorSystem: ActorSystem = ActorSystem("rest"),
                completerFactory: CompleterFactory = surf.CompleterFactory.PromiseCompleterFactory)
               (createRoute: (ExecutionContext,CompleterFactory,FlowMaterializer)=>server.Route) : SimpleRESTServer = {
    implicit val materializer = ActorFlowMaterializer()(actorSystem)

    val route = createRoute(actorSystem.dispatcher,completerFactory,materializer)

    new SimpleRESTServer(route,interface,port, ()=>actorSystem.shutdown())(actorSystem,completerFactory,materializer)
  }
}
