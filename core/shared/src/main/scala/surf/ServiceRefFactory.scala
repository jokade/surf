// -   Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: Defines the interface and default implementations for ServiceRefFactoryS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
// Description:
package surf

import java.util.concurrent.ExecutorService

import surf.service.{ExecutorServiceWrapper, AsyncServiceWrapper, SyncServiceWrapper}

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
   * Creates ServiceRefS that process messages asynchronously using a single thread.
   */
  implicit lazy val Async : ServiceRefFactory = new AsyncServiceRefFactory

  def fromExecutorService(es: ExecutorService): ServiceRefFactory = new ExecutorServiceRefFactory(es)

  final class SyncServiceRefFactory extends ServiceRefFactory {
    @inline
    final override def serviceOf(props: ServiceProps) = new SyncServiceWrapper(props.createService())
  }

  final class AsyncServiceRefFactory extends ServiceRefFactory {
    @inline
    final override def serviceOf(props: ServiceProps): ServiceRef = new AsyncServiceWrapper(props.createService())

    override def shutdown(): Unit = {
      java.util.concurrent.ForkJoinPool.commonPool().shutdownNow()
    }
  }

  final class ExecutorServiceRefFactory(es: ExecutorService) extends ServiceRefFactory {
    override def serviceOf(props: ServiceProps): ServiceRef = new ExecutorServiceWrapper(props.createService(),es)

    override def shutdown(): Unit = {
      es.shutdownNow()
    }
  }

}
