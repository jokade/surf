//     Project: surf (https://github.com/jokade/surf)
//      Module: tests
// Description: Common base class for integration tests.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test

import surf.{Service, ServiceRefFactory}
import utest._

abstract class IntegrationTestBase extends TestSuite with TaskTools {
  implicit def factory: ServiceRefFactory
}
