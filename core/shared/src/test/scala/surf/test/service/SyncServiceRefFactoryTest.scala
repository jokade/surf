//     Project: surf
//      Module: core / test / shared
// Description: Test suite for SyncServiceRefFactory

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.service

import surf.{CompletableFactory, ServiceProps, ServiceRef, ServiceRefFactory}

object SyncServiceRefFactoryTest extends ServiceRefBehaviour {
  override implicit val cf: CompletableFactory = surf.Implicits.globalCF
  override def createEUT(props: ServiceProps): ServiceRef = ServiceRefFactory.Sync.serviceOf(props)
}
