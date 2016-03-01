//     Project: surf
//      Module: tests
// Description: JVM-specific concurrency test suite for Service/ServiceRef.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.concurrency

import surf.test.AkkaServiceFixture

object AkkaServiceConcurrencyTest extends ServiceConcurrencyTest with AkkaServiceFixture

object AkkaDeadlockTest extends DeadlockTest with AkkaServiceFixture
