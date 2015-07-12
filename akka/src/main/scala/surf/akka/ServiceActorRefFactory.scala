// -   Project: surf (https://github.com/jokade/surf)
//      Module: akka
// Description: ServiceRefFactory for services that execute in Akka actors
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.akka

import akka.actor.{ActorRefFactory, Props}
import surf.{ServiceProps, ServiceRef, ServiceRefFactory}

class ServiceActorRefFactory(actorFactory: ActorRefFactory) extends ServiceRefFactory {

  override def serviceOf(props: ServiceProps): ServiceRef =
    ServiceActorRef( actorFactory.actorOf(ServiceActor(props)) )

}

object ServiceActorRefFactory {
  def apply(implicit factory: ActorRefFactory) : ServiceActorRefFactory = new ServiceActorRefFactory(factory)
}