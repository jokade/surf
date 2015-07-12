//     Project: surf (https://github.com/jokade/surf)
//      Module: core / tests / shared
// Description: Test cases for trait ServiceRefRegistry

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.service

import surf.ServiceRefRegistry.ServiceRefRegistryException
import surf._
import surf.test.TestBase
import utest._

trait ServiceRefRegistryBehaviour extends TestBase {
  import concurrent.ExecutionContext.Implicits.global
  def factory: ServiceRefFactory
  def createEUT(factory: ServiceRefFactory): ServiceRefRegistry

  val tests = TestSuite {
    val eut = createEUT(factory)
    assert( eut.registeredPaths.size == 0 )

    'registerServices-{
      val props = ServiceProps(new Service1)
      eut.registerServices(
        "/service1" -> props,
        "/test/service1" -> props
      )

      intercept[ServiceRefRegistryException]( eut.registerServices("/service1"->ServiceProps(new Service1)) )

      assert(
        eut.registeredPaths.size == 2,
        eut.registeredPaths.exists( _ == "/service1" ),
        eut.registeredPaths.exists( _ == "/test/service1" )
      )

      'serviceAt-{
        val s1 = eut.serviceAt("/service1")
        val t1 = eut.serviceAt("/test/service1")

        intercept[ServiceRefRegistryException]( eut.serviceAt("/test") )

        s1 ! 1
        t1 ! 42
        merge(
          (Request("get") >> s1).future.map{ case m => assert(m==1) },
          (Request("get") >> t1).future.map{ case m => assert(m==42) }
        )
      }
    }
  }


  class Service1 extends Service {
    private var data = 0
    override def process = {
      case i: Int => data = i
      case "get" => request ! data
    }
  }
}
