// -   Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: Defines the interface and default implementations for ServiceRefFactoryS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
// Description:
package surf

import surf.service.{AsyncServiceWrapper, SyncServiceWrapper}

/**
 * A factory for creating [[ServiceRef]]S using [[ServiceProps]]s.
 */
trait ServiceRefFactory {

  def serviceOf(props: ServiceProps) : ServiceRef

}

object ServiceRefFactory {

  /**
   * This factory creates ServiceRefS that process messages synchronously
   * on the thread from where the message is sent. Hence
   * ```service ! msg``` is equivalent to ```service.process(msg)```
   */
  implicit lazy val Sync : ServiceRefFactory = new SyncServiceRefFactory

  implicit lazy val Async : ServiceRefFactory = new AsyncServiceRefFactory

  class SyncServiceRefFactory extends ServiceRefFactory {
    override def serviceOf(props: ServiceProps) = new SyncServiceWrapper(props.createService())
  }

  class AsyncServiceRefFactory extends ServiceRefFactory {
    override def serviceOf(props: ServiceProps): ServiceRef = new AsyncServiceWrapper(props.createService())
  }

}
