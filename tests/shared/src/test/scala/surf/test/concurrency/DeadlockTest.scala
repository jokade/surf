//     Project: surf
//      Module: tests
// Description: Test suite to check if a Service/ServiceRef implementation is deadlock-safe.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.concurrency

import surf.Service
import surf.dsl._
import surf.test.{AsyncServiceFixture, IntegrationTestBase}
import utest._

abstract class DeadlockTest extends IntegrationTestBase {
  val refA = factory.serviceOf(new TestServiceA)
  val refB = factory.serviceOf(new TestServiceB)
  val tests = TestSuite{
    'a-{
      runTasks(
        () => for(i <- 1 to 1000) Ping >> refA onSuccess{ case x => assert(x.toString == "B")},
        () => for(i <- 1 to 1000) Ping >> refB onSuccess{ case x => assert(x.toString == "A")},
        () => for(i <- 1 to 1000) Ping >> refA onSuccess{ case x => assert(x.toString == "B")},
        () => for(i <- 1 to 1000) Ping >> refB onSuccess{ case x => assert(x.toString == "A")}
      )
    }
  }

  class TestServiceA extends Service {
    override def process = {
      case Ping => request.withInput(Pong) >> refB
      case Pong => request ! "A"
    }
  }

  class TestServiceB extends Service {
    override def process = {
      case Ping => request.withInput(Pong) >> refA
      case Pong => request ! "B"
    }
  }

  case object Ping
  case object Pong
}

// SyncServiceWrapper is not deadlock-safe
//object SyncDeadlockTest extends DeadlockTest with SyncServiceFixture

object AsyncDeadlockTest extends DeadlockTest with AsyncServiceFixture
