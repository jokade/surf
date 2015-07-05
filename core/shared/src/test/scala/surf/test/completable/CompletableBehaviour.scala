//     Project: surf (https://github.com/jokade/surf)
//      Module: core / test / shared
// Description: Test cases for trait Completable

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.completable

import surf.Completable
import surf.test.TestBase
import utest._

import scala.concurrent.{Promise, ExecutionContext}
import scala.util.{Try, Failure, Success}

/**
 * Test cases for 'normal' implementations of Completable (i.e. not for NullCompletable, etc.)
 */
trait CompletableBehaviour extends TestBase {
  implicit def executionContext: ExecutionContext
  def createEUT(): Completable

  val tests = TestSuite {
    val eut = createEUT()
    assert( !eut.isCompleted )

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

  }

}
