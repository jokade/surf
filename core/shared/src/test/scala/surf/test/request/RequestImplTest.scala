//     Project: surf (https://github.com/jokade/surf)
//      Module: core / test / shared
// Description: Tests for Request.Impl

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.request

import surf.{CompletableFactory, Request, Completable}
import surf.test.completable.CompletableBehaviour
import utest.TestSuite

import scala.concurrent.ExecutionContext

abstract class RequestImplTestFixture extends TestSuite {
  implicit def executionContext: ExecutionContext = surf.Implicits.globalCF.executionContext
  implicit def completableFactory: CompletableFactory = surf.Implicits.globalCF

  def createEUT(): Completable = Request(None)
  def createEUT(msg: Any, annotations: Map[String,Any]): Request = Request(msg,completableFactory.createCompletable(),annotations)
}

object RequestImplTest1 extends RequestImplTestFixture with CompletableBehaviour

object RequestImplTest2 extends RequestImplTestFixture with RequestBehaviour
