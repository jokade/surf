//     Project:
//      Module:
// Description:
package surf.akka

import akka.actor.ActorRef
import surf.{ServiceRef, Directives}

import scala.language.implicitConversions

object AkkaDirectives extends Directives {

  implicit def actorRefToServiceRef(actorRef: ActorRef) : ServiceRef = ServiceActorRef(actorRef)

  implicit class ActorRefDSL(actorRef: ActorRef) extends ServiceDSL(ServiceActorRef(actorRef)) {
    //def ::(left: ActorRef)
  }

}
