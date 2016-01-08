//     Project: surf (https://github.com/jokade/surf)
//      Module: core / test / shared
// Description: Test cases for trait Request

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.request

import surf.test.TestBase
import surf.Request
import utest._

import scala.concurrent.{Promise, Future, ExecutionContext}
import scala.util.{Success, Failure}

trait RequestBehaviour extends TestBase {
  implicit def executionContext: ExecutionContext
  def createEUT(msg: Any, annotations: Map[String,Any]): Request

  val tests = TestSuite {
    val eut = createEUT("msg", Map("int"->42))

    'success-{
      val f = eut.future
      eut.success("OK")
      f.map {
        case msg => assert( msg == "OK", eut.isCompleted )
      }
    }

    'failure-{
      val f = eut.future
      eut.failure(Ex)
      expectFailure(f)
    }

    'onComplete-{
      'success-{
        val p = Promise[Any]()
        eut.onComplete{ case Success(msg) =>
          assert( msg == "OK", eut.isCompleted )
          p.success(true)
        }
        eut.success("OK")
        p.future
      }
      'failure-{
        val p = Promise[Any]()
        eut.onComplete{ case Failure(Ex) => p.success(true) }
        eut.failure(Ex)
        p.future
      }
    }

    'onSuccess_onFailure-{
      'success-{
        val p = Promise[Any]()
        eut.onSuccess{ case msg => assert( msg == "OK", eut.isCompleted ); p.success(true) }
        eut.onFailure{ case _ => p.failure(Ex) }
        eut.success("OK")
        p.future
      }
      'failure-{
        val p = Promise[Any]()
        eut.onFailure{ case Ex => p.success(true) }
        eut.onSuccess{ case _ => p.failure(Ex) }
        eut.failure(Ex)
        p.future
      }
    }


    'input-{
      assert( eut.input == "msg" )
    }

    'annotations-{
      assert( eut.annotations("int") == 42 )
    }

    'withAnnotations-{
      assert( eut.annotations("int") == 42 )
      val updated = eut.withAnnotations{ m =>
        assert( m.size == 1, m("int") == 42 )
        m + ("bool" -> true)
      }

      assert( updated.annotations.size == 2, updated.annotations("int") == 42, updated.annotations("bool") == true )
    }

    '!{
      assert( ! eut.isCompleted )
      eut ! "OK"
      eut.future.map{ case msg => assert( eut.isCompleted, msg == "OK" ) }
    }

    'withInput-{
      val mapped = eut.withInput(42)
      assert( eut.input == "msg", !eut.isCompleted,
              mapped.input == 42, !mapped.isCompleted )

      "!eut"-{
        completeFirst(eut,mapped)
      }

      "!mapped"-{
        completeFirst(mapped,eut)
      }
    }

    'mapInput-{
      val mapped = eut.mapInput{
        case "msg" => 43
      }
      assert( !eut.isCompleted, eut.input == "msg",
              !mapped.isCompleted, mapped.input == 43 )

      "!eut"-{
        completeFirst(eut,mapped)
      }

      "!mapped"-{
        completeFirst(mapped,eut)
      }
    }

    'mapOutput-{
      val mapped = eut.mapOutput{ case "msg" => 44; case x => x }
      val mapped2 = mapped.mapOutput{ case 44 => true }
      assert( !eut.isCompleted, !mapped.isCompleted, !mapped2.isCompleted )

      "!eut"-{
        val f1 = eut.future.map{ case m => assert(m=="msg") }
        val f2 = mapped.future.map{ case m => assert(m == "msg") }
        val f3 = mapped2.future.map{ case m => assert(m == "msg") }
        eut ! "msg"
        merge(f1,f2,f3)
      }

      "!mapped"-{
        val f1 = eut.future.map{ case m => assert(m==44) }
        val f2 = mapped.future.map{ case m => assert(m==44) }
        val f3 = mapped2.future.map{ case m => assert(m==44) }
        mapped ! "msg"
        merge(f1,f2,f3)
      }

      "!mapped2"-{
        val f1 = eut.future.map{ case m => assert(m==true) }
        val f2 = mapped.future.map{ case m => assert(m==true) }
        val f3 = mapped2.future.map{ case m => assert(m==true) }
        mapped2 ! "msg"
        merge(f1,f2,f3)
      }
    }
  }


  private def completeFirst(first: Request, second: Request): Future[Any] = {
    assert( !first.isCompleted, !second.isCompleted )
    val f1 = first.future
    val f2 = second.future
    first ! "OK"
    Future.reduce(Seq(f1,f2))((_,msg)=> assert( msg == "OK") ).map{_ =>
      assert( first.isCompleted, second.isCompleted )
    }
  }

}
