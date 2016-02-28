//     Project: surf (https://github.com/jokade/surf)
//      Module: tests
// Description: Fixture for tests using the FutureServiceRefFactory

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test

import surf.ServiceRefFactory

trait FutureServiceFixture {
  implicit def factory: ServiceRefFactory = ServiceRefFactory.Async
  implicit def executionContext = concurrent.ExecutionContext.global
}
