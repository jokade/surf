//     Project: surf (https://github.com/jokade/surf)
//      Module: tests
// Description: Fixture for tests using the Akka ServiceActorRefFactory.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test

import akka.actor.ActorSystem
import surf.ServiceRefFactory
import surf.akka.ServiceActorRefFactory

trait AkkaServiceFixture {
  lazy val actorSystem = ActorSystem("AkkaServiceFixture")
  implicit def factory: ServiceRefFactory = ServiceActorRefFactory(actorSystem)
  implicit def executionContext = concurrent.ExecutionContext.global
}
