//     Project: surf (https://github.com/jokade/surf)
//      Module:
// Description:

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.akka.test

import akka.actor.ActorSystem
import surf.akka.ServiceActorRefFactory
import surf.test.service.ServiceRefBehaviour
import surf.{ServiceProps, ServiceRef}

object ServiceActorRefFactoryTest extends ServiceRefBehaviour {
  val actorSystem = ActorSystem("ServiceActorRefFactoryTest")
  val factory = ServiceActorRefFactory(actorSystem)
  implicit def ec = actorSystem.dispatcher

  override def createEUT(props: ServiceProps): ServiceRef = factory.serviceOf(props)

}
