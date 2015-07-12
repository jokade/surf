//     Project: surf
//      Module: core / test / shared
// Description: Test suite for SyncServiceRefFactory

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.service

import surf.{ServiceProps, ServiceRef, ServiceRefFactory}

object SyncServiceRefFactoryTest extends ServiceRefBehaviour {
  implicit def ec = concurrent.ExecutionContext.global
  override def createEUT(props: ServiceProps): ServiceRef = ServiceRefFactory.Sync.serviceOf(props)
}
