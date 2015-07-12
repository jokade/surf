//     Project: surf (https://github.com/jokade/surf)
//      Module: core / test / shared
// Description: Test suite for the surf request flow DSL

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.dsl

import surf.ServiceRefFactory.SyncServiceRefFactory
import surf._
import surf.test.TestBase
import surf.dsl._
import utest._

import scala.concurrent.ExecutionContext

object DSLTest extends DSLBehaviour {
  implicit def ec = concurrent.ExecutionContext.global
  val factory = new SyncServiceRefFactory
  def serviceRef(s: => Service): ServiceRef = factory.serviceOf(ServiceProps(s))
}


trait DSLBehaviour extends TestBase {
  implicit def ec: ExecutionContext
  def serviceRef(s: => Service): ServiceRef
  val tests = TestSuite {
    val incService = serviceRef(new IncService)
    val add1Service = serviceRef(new IncService)

    'implicitAnyToRequest-{
      val req = (1 >> incService) mapOutput{
        case 2 => true
        case _ => false
      }
      req.future.map{ case m => assert(m==true) }
    }

    'simpleServicePipe-{
      val add3Service = incService :: add1Service
      add3Service ! 2

      val req = 1 >> add3Service mapOutput{
        case 4 => true
        case _ => false
      }
      //req.future.map{ case m => assert(m==true) }
    }
  }


  class IncService extends Service {
    private var _increment = 1
    def process = {
      case i: Int if isRequest => request ! i+_increment
      case i: Int => _increment = i
    }
  }

}
