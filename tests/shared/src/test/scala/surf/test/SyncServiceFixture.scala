//     Project: surf (https://github.com/jokade/surf)
//      Module: tests
// Description: Fixture for tests using the SyncServiceWrapper

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test

import surf.ServiceRefFactory

trait SyncServiceFixture {
  implicit def factory: ServiceRefFactory = ServiceRefFactory.Sync
  implicit def executionContext = concurrent.ExecutionContext.global
}
