//     Project: surf (https://github.com/jokade/surf)
//      Module: core / test / shared
// Description: Tests for Request.Impl

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.request

import surf.Request

import scala.concurrent.{ExecutionContext, Promise}

object RequestImplTest extends RequestBehaviour {
  implicit def executionContext: ExecutionContext = concurrent.ExecutionContext.global

  def createEUT(msg: Any, annotations: Map[String,Any]): Request = Request(msg,Promise[Any](),annotations)
}

