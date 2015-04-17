// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines the interface and default implementations for ServiceRefFactoryS
//
// Copyright (c) 2015 Johannes Kastner <jkspam@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
// Description:
package surf

import surf.service.SyncServiceWrapper

/**
 * A factory for creating [[ServiceRef]]S using [[ServiceProps]]s.
 */
trait ServiceRefFactory {

  def serviceOf(props: ServiceProps) : ServiceRef

}

object ServiceRefFactory {

  lazy val Static : ServiceRefFactory = new StaticServiceRefFactory

  class StaticServiceRefFactory extends ServiceRefFactory {
    override def serviceOf(props: ServiceProps) = new SyncServiceWrapper(props.createService())
  }

}
