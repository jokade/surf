// -   Project: surf (https://github.com/jokade/surf)
//      Module:
// Description:
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.test

import surf.{ServiceRefFactory, CompleterFactory, ServiceProps, ServiceRef}

import scala.concurrent.ExecutionContext

object SyncServiceWrapperTest extends ServiceRefTest {
  override implicit val ec = ExecutionContext.global
  override implicit val cf = CompleterFactory.Implicits.globalCF

  override def serviceOf(props: ServiceProps): ServiceRef = ServiceRefFactory.Static.serviceOf(props)
}

