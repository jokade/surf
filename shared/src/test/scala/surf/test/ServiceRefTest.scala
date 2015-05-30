// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared / test
// Description: Basic test suite for ServiceRef implementations
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.test

import surf.MessageProcessor.Processor
import surf.{ServiceRef, ServiceProps, Service, CompleterFactory}
import surf.Directives._
import utest._

import scala.concurrent.ExecutionContext

trait ServiceRefTest extends TestSuite {

  implicit def ec: ExecutionContext
  implicit def cf: CompleterFactory
  def serviceOf(props: ServiceProps) : ServiceRef

  private var lastCommand = ""

  override val tests = TestSuite {
    val service = serviceOf(ServiceProps(new Service1))
    lastCommand = ""

    val x =  service :: (_:Any) match {case x => Next(x)} :: service

    'message-{
      service ! Cmd("test")
      assert( lastCommand == "test" )
    }

    'request-{
      (Req(42) >> service).future.map{
        case Resp(42) => true
        case _ => ???
      }
    }
  }


  case class Cmd(cmd: String)
  case class Req(i: Int)
  case class Resp(i: Int)

  class Service1 extends Service {
    override def process: Processor = {
      case Req(i) if isRequest => request ! Resp(i)
      case Cmd(cmd) => lastCommand = cmd
      case _ => ???
    }
  }
}
