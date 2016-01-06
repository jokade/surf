//     Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: Directives and utility functions for the surf request flow DSL

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

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
   *
   * @note This method is not supported in JavaScript environments!
   */
  final def await(req: Request)(implicit atMost: Duration) : Any = Await.result(req.future,atMost)

  /**
   * Waits until the request is completed and then maps its result using the specified function.
   *
   * @param req request to wait for
   * @param pf the request result is mapped using this function
   * @param atMost timeout (an exception is thrown, if the request is not completed within this duration)
   * @tparam T result type
   *
   * @note This method is not supported in JavaScript environments!
   */
  final def awaitMap[T](req: Request)(pf: PartialFunction[Any,T])(implicit atMost: Duration) : T =
    pf(Await.result(req.future,atMost))

  /**
   * Converts Any followed by a `>> ServiceRef` into a Request and sends the Request to the ServiceRef.
   * @param data
   */
  implicit class AnyToRequest(val data: Any) extends AnyVal {
    def >>(ref: ServiceRef)(implicit ec: ExecutionContext) : Request = {
      ref ! Request(data)
    }
  }

  /**
   * Converts a ServiceRef into a PipelineService
   */
  implicit class ServiceDSL(val service: ServiceRef) extends AnyVal {
    def ::(left: ServiceRef) : ServicePipe = left :: service :: PipeEnd
  }

  object transform {
    /**
     * Creates a ServiceRef that transforms the message of every request received.
     *
     * @param pf function used to transform the request message
     */
    def apply(pf: PartialFunction[Any,Any]) : ServiceRef = new Impl(pf)

    private class Impl(pf: PartialFunction[Any,Any]) extends ServiceRef {
      override def !(req: Request): Request = {
        req ! pf(req.input)
        req
      }
      override def !(msg: Any): Unit =
        throw new RuntimeException(s"Cannot transform a command message (only Requests can be transformed)")
    }
  }

  object annotate {
    /**
     * Adds the specified annotations to every Request received
     *
     * @param annotations (key,value) pairs to be added as annotations to every Request
     */
    def apply(annotations: (String,Any)*) : ServiceRef = new Impl(m => m++annotations)

    /**
     * Updates the map of annotations for every request received.
     *
     * @param f function that receives the current map of annotations and returns the updated map.
     */
    def apply(f: Map[String,Any]=>Map[String,Any]) : ServiceRef = new Impl(f)

    protected[dsl] case class Impl(f: Map[String,Any]=>Map[String,Any]) extends ServiceRef {
      override def !(req: Request): Request = {
        req.withAnnotations(f)
      }
      override def !(msg: Any): Unit =
        throw new RuntimeException(s"Cannot annotate a command message (only Requests can be annotated)")
    }
  }

  object handle {
    def apply(f: Request => Request) : ServiceRef = Impl(f)

    protected[dsl] case class Impl(f: Request=>Request) extends ServiceRef {
      override def !(req: Request): Request = f(req)
      override def !(msg: Any): Unit =
        throw new RuntimeException(s"Cannot handle a command message (only Requests can be handled)")
    }
  }
}
