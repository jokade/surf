//     Project:
//      Module:
// Description:
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