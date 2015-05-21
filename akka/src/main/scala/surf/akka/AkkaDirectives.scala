// -   Project: surf (https://github.com/jokade/surf)
//      Module: akka
// Description:
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.akka

import akka.actor.ActorRef
import surf.{ServiceRef, Directives}

import scala.language.implicitConversions

// TODO: do we still need this object?
object AkkaDirectives extends Directives {

  implicit def actorRefToServiceRef(actorRef: ActorRef) : ServiceRef = ServiceActorRef(actorRef)

  implicit class ActorRefDSL(actorRef: ActorRef) extends ServiceDSL(ServiceActorRef(actorRef)) {
    //def ::(left: ActorRef)
  }

}
