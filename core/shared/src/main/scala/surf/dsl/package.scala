//     Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: Directives and utility functions for the surf request flow DSL

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * Directives for the surf request flow DSL.
 */
package object dsl {
  private var _defaultTimeout: Duration = Duration(5,"seconds")

  /**
   * Default time out for blocking operations.
   */
  implicit final def defaultTimeout: Duration = _defaultTimeout
  final def defaultTimeout_=(d: Duration) = this.synchronized{ _defaultTimeout = d }

  /**
   * Waits until the request is completed an then returns its result.
   *
   * @param req request to wait for
   * @param atMost timeout (an exception is thrown, if the request is not completed within this duration)
   */
  final def await(req: Request)(implicit atMost: Duration) : Any = Await.result(req.future,atMost)

  /**
   * Waits until the request is completed and then maps its result using the specified function.
   *
   * @param req request to wait for
   * @param pf the request result is mapped using this function
   * @param atMost timeout (an exception is thrown, if the request is not completed within this duration)
   * @tparam T result type
   */
  final def awaitMap[T](req: Request)(pf: PartialFunction[Any,T])(implicit atMost: Duration) : T =
    pf(Await.result(req.future,atMost))

  /**
   * Converts Any followed by a `>> ServiceRef` into a Request and sends the Request to the ServiceRef.
   * @param data
   */
  implicit class AnyToRequest(val data: Any) extends AnyVal {
    def >>(ref: ServiceRef)(implicit cf: CompletableFactory) : Request = {
      ref ! Request(data)
    }
  }

  /**
   * Converts a ServiceRef into a PipelineService
   */
  implicit class ServiceDSL(val service: ServiceRef) extends AnyVal {
    def ::(left: ServiceRef) : ServicePipe = left :: service :: PipeEnd
  }

}
