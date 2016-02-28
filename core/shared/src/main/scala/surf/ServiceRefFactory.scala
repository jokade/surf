// -   Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: Defines the interface and default implementations for ServiceRefFactoryS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
// Description:
package surf

import surf.service.{FutureServiceRef, SyncServiceWrapper}

import scala.concurrent.ExecutionContext

/**
 * A factory for creating [[ServiceRef]]S.
 */
trait ServiceRefFactory {

  def serviceOf(props: ServiceProps) : ServiceRef

  @inline
  final def serviceOf(createService: =>Service) : ServiceRef = serviceOf(ServiceProps(createService))

  /**
   * Terminates this factory.
   */
  def shutdown(): Unit = {}
}

object ServiceRefFactory {

  /**
   * This factory creates ServiceRefS that process messages synchronously
   * on the thread from where the message is sent. Hence
   * ```service ! msg``` is equivalent to ```service.process(msg)```
   */
  implicit lazy val Sync : ServiceRefFactory = new SyncServiceRefFactory

  /**
   * This factory creates ServiceRefS that process messages asynchronously
   * using Futures and the global ExecutionContext.
   */
  implicit lazy val Async : ServiceRefFactory = new FutureServiceRefFactory(ExecutionContext.global)

  /**
   * Creates a ServiceRefFactory for ServiceRefS that process messages asynchronously using Futures
   * and the provided ExecutionContext.
   */
  def futures(executionContext: ExecutionContext) : ServiceRefFactory = new FutureServiceRefFactory(executionContext)

  final class SyncServiceRefFactory extends ServiceRefFactory {
    @inline
    final override def serviceOf(props: ServiceProps) = new SyncServiceWrapper(props.createService())
  }

  final class FutureServiceRefFactory(ec: ExecutionContext) extends ServiceRefFactory {
    override def serviceOf(props: ServiceProps): ServiceRef = new FutureServiceRef(props.createService())(ec)
  }
}
