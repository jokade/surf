//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared / test
// Description: Tests for common RESTResolver implementations.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest.test

import surf.rest.test.SimpleRESTResolver.LoggingResolver
import surf.rest.{RESTAction, RESTResolver}
import surf.test.TestBase
import surf.{Service, ServiceProps, ServiceRef, ServiceRefFactory}
import utest._

object RESTResolverWrapperTest extends TestBase {
  import SimpleRESTResolver._

  override val tests = TestSuite {
    'empty-{
      val eut = new WrapperDummy(Seq())
      assert( eut.resolveRESTService(getFooBar) == None, eut.called )
    }

    'single-{
      val eut = new WrapperDummy(Seq(SimpleRESTResolver(getFooBar)))
      'a-{
        assert(
          eut.resolveRESTService(getFooBar) == Some((dummyService,getFooBar)),
          eut.called,
          eut.subresolvers(0).called
        )
      }
      'b-{
        assert(
          eut.resolveRESTService(getFooBarSub) == None,
          eut.called,
          eut.subresolvers(0).called
        )
      }
    }

    'multiple-{
      val eut = new WrapperDummy(Seq(SimpleRESTResolver(getFooBar),SimpleRESTResolver(getFooBarSub),SimpleRESTResolver(getHello)))
      'a-{
        assert( eut.resolveRESTService(getFooBar) == Some((dummyService,getFooBar)),
          eut.called,
          eut.subresolvers(0).called,
          !eut.subresolvers(1).called,
          !eut.subresolvers(2).called
        )
      }
      'b-{
        assert( eut.resolveRESTService(getFooBarSub) == Some((dummyService,getFooBarSub)),
          eut.called,
          eut.subresolvers(0).called,
          eut.subresolvers(1).called,
          !eut.subresolvers(2).called
        )
      }
      'b-{
        assert( eut.resolveRESTService(getHelloWorld) == None,
          eut.called,
          eut.subresolvers(0).called,
          eut.subresolvers(1).called,
          eut.subresolvers(2).called
        )
      }
    }
  }

  class WrapperDummy(val subresolvers: Seq[LoggingResolver]) extends RESTResolver.Wrapper with LoggingResolver {
    override def resolveRESTService(action: RESTAction): Option[(ServiceRef, RESTAction)] = {
      called = true
      super.resolveRESTService(action)
    }
  }
}


object PrefixResolverTest extends TestBase {
  import RESTResolver.PrefixResolver
  import SimpleRESTResolver._

  override val tests = TestSuite {
    'empty-{
      val eut = new PrefixResolver(Map())
      assert( eut.resolveRESTService(getFooBar) == None )
    }
    'prefix-{
      val map = Map(
        Seq("foo") -> SimpleRESTResolver(getBar,dummyService),
        Seq("hello") -> SimpleRESTResolver(getWorld,dummyService)
      )
      val eut = new PrefixResolver(map)
      'A-{
        assert(
          eut.resolveRESTService(getFooBar) == Some((dummyService,getBar)),
          map(Seq("foo")).called,
          !map(Seq("hello")).called
        )
      }
      'B-{
        assert(
          eut.resolveRESTService(getHelloWorld) == Some((dummyService,getWorld)),
          !map(Seq("foo")).called,
          map(Seq("hello")).called
        )
      }
      'C-{
        assert(
          eut.resolveRESTService(getBar) == None,
          !map(Seq("foo")).called,
          !map(Seq("hello")).called
        )
      }
    }
  }

}


case class SimpleRESTResolver(matchedAction: RESTAction, service: ServiceRef = SimpleRESTResolver.dummyService) extends LoggingResolver {
  def resolveRESTService(action: RESTAction): Option[(ServiceRef, RESTAction)] = {
    called = true
    if (action == matchedAction) Some((service, action))
    else None
  }
}

object SimpleRESTResolver {
  trait LoggingResolver extends RESTResolver {
    var called: Boolean = false
  }

  val dummyService = ServiceRefFactory.Sync.serviceOf(ServiceProps(new Service {
    override def process = ???
  }))
  val getFooBar = RESTAction.GET(Seq("foo","bar"),Map())
  val getFooBarSub = RESTAction.GET(Seq("foo","bar","sub"),Map())
  val getHello = RESTAction.GET(Seq("hello"),Map())
  val getHelloWorld = RESTAction.GET(Seq("hello","world"),Map())
  val getBar = RESTAction.GET(Seq("bar"),Map())
  val getWorld = RESTAction.GET(Seq("world"),Map())

}