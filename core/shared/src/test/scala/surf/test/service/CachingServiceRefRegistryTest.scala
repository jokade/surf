//     Project: surf (https://github.com/jokade/surf)
//      Module: core / test / shared
// Description: Test suite for CachingServiceRefRegistry

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.service

import surf.ServiceRefFactory.SyncServiceRefFactory
import surf.ServiceRefRegistry.CachingServiceRefRegistry
import surf.{ServiceRefRegistry, ServiceRefFactory}

object CachingServiceRefRegistryTest extends ServiceRefRegistryBehaviour {
  override val factory: ServiceRefFactory = new SyncServiceRefFactory

  override def createEUT(factory: ServiceRefFactory): ServiceRefRegistry =
    new CachingServiceRefRegistry(factory,Map())
}
