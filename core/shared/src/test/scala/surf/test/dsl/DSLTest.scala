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

  val stringToInt = transform{
    case s: String => s.toInt
  }

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
      val add3Service = incService :: add1Service :: incService
      add3Service ! 2

      val req = 1 >> add3Service mapOutput{
        case 6 => true
        case _ => false
      }
      req.future.map{ case m => assert(m==true) }
    }


    'transform-{
      val req = "41" >> stringToInt :: incService :: transform{
        case x => x.toString
      } :: stringToInt mapOutput{
        case 42 => true
      }
      req.future.map{ case m => assert(m==true) }
    }


    'annotate-{
      val req1 = 1 >> annotate("test"->true)
      assert(req1.annotations("test") == true)

      val req2 = req1 >> annotate("int"->42)
      assert( req2.annotations("test") == true, req2.annotations("int") == 42 )

      val req3 = req2 >> annotate( _.updated("test",false) )
      assert( req3.annotations("test") == false, req3.annotations("int") == 42 )

      val req4 = "msg" >> annotate("int"->41) :: add1Service mapOutput{
        case 42 => true
      }
      val f1 = req4.future.map{ case m => assert(m==true) }

      val req5 = "1" >> stringToInt :: annotate("int"->42) :: add1Service mapOutput{
        case 43 => true
      }
      val f2 = req5.future.map{ case m => assert(m==true) }

      merge(f1,f2)
    }

    'handle-{
      val req1 = "msg" >> handle{ req => req.withAnnotations(_ => Map("int"->42)) }
      assert( req1.input == "msg", req1.annotations("int") == 42 )

      val req2 = "msg" >> handle( _.mapInput {
        case "msg" => 1
      }) :: add1Service mapOutput{
        case 2 => true
      }

      req2.future.map{ case m => assert( m == true ) }
    }
  }


  class IncService extends Service {
    private var _increment = 1
    def process = {
      case _ if request.annotations.contains("int") =>
        val i = request.annotations("int").asInstanceOf[Int]
        request ! i + _increment
      case i: Int if isRequest => request ! i+_increment
      case i: Int => _increment = i
    }
  }

}
