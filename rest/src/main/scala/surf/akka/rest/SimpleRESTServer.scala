//     Project:
//      Module:
// Description:
package surf.akka.rest

import surf.akka.ServiceActorRefFactory

import scala.language.implicitConversions
import akka.actor.ActorSystem
import akka.http.{server, Http}
import akka.http.server.Directives._
import akka.stream.FlowMaterializer
import surf.{Request, CompleterFactory}
import surf.rest.{RESTRequest, StaticRESTResource, RESTResource}

import scala.concurrent.{ExecutionContext, Future}

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

  val binding = Http().bind(interface,port)

  val materializedMap = binding startHandlingWith route

  def stop() = binding.unbind(materializedMap).onComplete( _ => afterStop() )
}

object SimpleRESTServer {
  def apply(rootResources: Iterable[RESTResource],
            interface: String = "127.0.0.1",
            port: Int = 8081,
            prefix: String = "rest")
           (implicit cf: CompleterFactory) : SimpleRESTServer = {

    implicit val system = ActorSystem("rest")
    import system.dispatcher

    implicit val materializer = FlowMaterializer()
    implicit val serviceRefFactory = ServiceActorRefFactory(system)

    val root = StaticRESTResource("rest",rootResources)

    new SimpleRESTServer( RESTRouter(prefix,root) ,interface,port, () => system.shutdown())(system,cf,materializer)
  }

  def fromRoute(interface: String = "127.0.0.1",
                port: Int = 8081,
                actorSystem: ActorSystem = ActorSystem("rest"),
                completerFactory: CompleterFactory = surf.CompleterFactory.PromiseCompleterFactory)
               (createRoute: (ExecutionContext,CompleterFactory,FlowMaterializer)=>server.Route) : SimpleRESTServer = {
    implicit val materializer = FlowMaterializer()(actorSystem)

    val route = createRoute(actorSystem.dispatcher,completerFactory,materializer)

    new SimpleRESTServer(route,interface,port, ()=>actorSystem.shutdown())(actorSystem,completerFactory,materializer)
  }
}
