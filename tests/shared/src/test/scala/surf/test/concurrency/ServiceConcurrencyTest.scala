//     Project: surf (https://github.com/jokade/surf)
//      Module: tests
// Description: Concurrency test suite for Service/ServiceRef.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.concurrency

import java.util.ConcurrentModificationException

import surf.Service
import surf.dsl._
import surf.test.{AsyncServiceFixture, IntegrationTestBase, SyncServiceFixture}
import utest._

import scala.util.Success

abstract class ServiceConcurrencyTest extends IntegrationTestBase {
  val ref = factory.serviceOf(new TestService)
  val ntasks = 4
  val niter = 10000
  val ntot = ntasks*niter

  val tests = TestSuite {
    'concurrentMessages-{
      ref ! Set(0)
      time(
        runTasks(ntasks){
          for(i <-1 to niter) {
            ref ! Ping
          }
        } flatMap {
          case _ => (Get >> ref).future
        } map {
          case acc: Int => assert( acc == ntot )
        }){nanos =>
        println(f"concurrentMessages: processed $ntot messages in ${nanos/1e6}%.2f ms (${ntot*1e9/nanos}%.1e messages/s)")
      }
    }
    'concurrentRequests-{
      ref ! Set(0)
      time(
      runTasks(ntasks){
        for(i <-1 to niter) {
          Ping >> ref onComplete{ case Success(i:Int) => () }
        }
      } flatMap {
           case _ => (Get >> ref).future
        } map {
          case acc: Int => assert( acc == ntot )
        }){nanos =>
        println(f"concurrentRequests: processed $ntot messages in ${nanos/1e6}%.2f ms (${ntot*1e9/nanos}%.1e messages/s)")
      }
    }
  }

  class TestService extends Service {
    private var acc = 0

    override def process = {
      case Ping =>
        val cur = acc
        acc += 1
        if(acc != cur+1)
          throw new ConcurrentModificationException
        if(isRequest) request ! acc
      case Get if isRequest => request ! acc
      case Set(i) => acc = i
    }
  }

  case object Ping
  case object Get
  case class Set(acc: Int)
  case class Value(acc: Int)
}

object SyncServiceConcurrencyTest extends ServiceConcurrencyTest with SyncServiceFixture

object AsyncServiceConcurrencyTest extends ServiceConcurrencyTest with AsyncServiceFixture

