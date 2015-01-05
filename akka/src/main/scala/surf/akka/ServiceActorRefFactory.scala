// -   Project: surf (https://github.com/jokade/surf)
//      Module: akka
// Description: ServiceRefFactory for services that executed in Akka actors
//
// Copyright (c) 2015 Johannes Kastner <jkspam@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.akka

import akka.actor.{Props, ActorRefFactory}
import surf.{ServiceRef, ServiceProps, ServiceRefFactory}

class ServiceActorRefFactory(actorFactory: ActorRefFactory) extends ServiceRefFactory {
  override def serviceOf(props: ServiceProps): ServiceRef = ServiceActorRef{
    actorFactory.actorOf(Props( new ServiceWrapperActor(props.createService()) ))
  }
}

object ServiceActorRefFactory {
  def apply(implicit factory: ActorRefFactory) : ServiceActorRefFactory = new ServiceActorRefFactory(factory)
}