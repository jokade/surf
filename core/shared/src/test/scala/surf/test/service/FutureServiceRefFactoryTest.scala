//     Project: surf (https://github.com/jokade/surf)
//      Module: core / test / shared
// Description: Test suite for FutureServiceRefFactory

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.service

import surf.{ServiceRefFactory, ServiceRef, ServiceProps}

object FutureServiceRefFactoryTest extends ServiceRefBehaviour {
  implicit def ec = concurrent.ExecutionContext.global
  override def createEUT(props: ServiceProps): ServiceRef = ServiceRefFactory.Async.serviceOf(props)
}
