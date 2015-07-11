//     Project: surf
//      Module: core / test / shared
// Description: Test cases for trait ServiceRef

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.service

import surf.test.TestBase
import surf._
import utest._

import scala.concurrent.Promise

trait ServiceRefBehaviour extends TestBase {
  implicit val cf: CompletableFactory
  import cf.executionContext
  def createEUT(props: ServiceProps) : ServiceRef

  val tests = TestSuite {
    val unhandled = Promise[Any]()
    val eut = createEUT(ServiceProps(new TestService(unhandled)))

    'message-{
      'success-{
        val cmd1 = Cmd("OK")
        eut ! cmd1
        cmd1.promise.future.map{ case m => assert(m=="OK") }
      }

      'unhandled-{
        eut ! "unhandled"
        unhandled.future
      }
    }

    'request-{
      'success-{
        val req = Request(41)
        eut ! req
        req.future.map{ case m => assert(m==42) }
      }

      'unhandled-{
        val req = Request("unhandled")
        eut ! req
        expectFailure(req.future)
      }
    }
  }

  case class Cmd(msg: String) {
    val promise = Promise[String]()
    def complete() = promise.success(msg)
    def fail() = promise.failure(new RuntimeException)
  }

  class TestService(unhandled: Promise[Any]) extends Service {
    override def handleException(ex: Throwable) = unhandled.success(ex)

    def process = {
      case cmd : Cmd =>
        if(isRequest) cmd.fail() else cmd.complete()
      case i: Int if isRequest => request ! i+1
    }
  }
}
