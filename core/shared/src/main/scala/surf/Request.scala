// -   Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: Defines the Request trait and default implementations
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included LICENSE file)
package surf

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Success, Try}

/**
 * A completable request that provides access to the corresponding response in a future.
 */
trait Request {
  type Response = Try[Any]

  /**
   * The request message.
   */
  def input: Any

  /**
   * Map with all annotations defined on the current request.
   */
  def annotations: Map[String,Any]

  /**
   * Creates an updated request with additional annotations.
   *
   * @param f Receives the annotations defined on the current request and returns the updated map of annotations.
   */
  def withAnnotations(f: Map[String,Any]=>Map[String,Any]) : Request

  /**
   * Completes the current request successfully with the specified response.
   *
   * @param response Response data
   */
  @inline final def !(response: Any) : Unit = success(response)

  /**
   * Sends the current request to the specified service.
   *
   * @param service Service to which this request is sent for processing
   *
   * @return the response (request object returned by the service).
   */
  @inline final def >>(service: ServiceRef) : Request = service ! this

  /**
   * Creates an updated request from the current one, where [[input]] is replaced with the
   * result from `fInput(input)`.
   *
   * @param f
   * @return updated reuqest
   */
  def mapInput(f: (Any)=>Any) : Request = map(f)( x=>x )

  /**
   * Creates an updated request from the current one, where [[input]] is replaced
   * with the specified value.
   *
   * @param input value for [[input]] in updated request
   */
  def withInput(input: Any) : Request = mapInput( _ => input)

  /**
   * Creates an updated request from the current one, where the response will be transformed by sepcified function.
   *
   * @param f function used to transform the response message
   *
   * @see [[map()]]
   */
  def mapOutput(f: (Any)=>Any) : Request = map( x=>x )(f)

  /**
   * Alias for [[mapOutput()]] (used in the DSL)
   *
   * @param f function used to transform the response massage __before__ it is used to complete the request.
   */
  //@inline final def >>(f: PartialFunction[Any,Any]) : Request = mapOutput(f)

  /**
   * Returns a new request with the same completion target as the current request,
   * but the original input is transformed by `fIn`, and the response is transformed by `fOut`.
   *
   * @param fIn function to transform the input message with
   * @param fOut function to transform the response with
   */
  def map(fIn: (Any) => Any)(fOut: (Any) => Any) : Request

  /**
   * Returns true iff the request has been completed
   */
  def isCompleted: Boolean

  /**
   * Completes the request (flow) with the provided Response.
   *
   * @note A request can only be completed once!
   *
   * @param resp either a successful response or a failure.
   *
   * @throws RequestAlreadyCompletedException if the request has already been completed
   */
  def complete(resp: Response) : Unit

  /**
   * Completes the request (flow) with a failure
   *
   * @param ex Exception indicating the failure
   *
   * @throws RequestAlreadyCompletedException if the request has already been completed
   */
  @inline final def failure(ex: Throwable) : Unit = complete(Failure(ex))

  /**
   * Completes the request (flow) successfully with the provided response result.
   *
   * @param result The result data object
   *
   * @throws RequestAlreadyCompletedException if the request has already been completed
   */
  @inline final def success(result: Any) : Unit = complete(Success(result))

  /**
   * Returns a Future that will be completed when this request (flow) is completed.
   */
  def future : Future[Any]

  /**
   * The provided function is called when the request is completed
   *
   * @param f function to be called on completion
   */
  def onComplete(f: PartialFunction[Try[Any],Any]) : Request

  /**
   * The specified function is called when the request is completed successfully
   *
   * @param f function to be called on successful completion
   */
  def onSuccess(f: PartialFunction[Any,Any]) : Request

  /**
   * The specified function is called when the request fails.
   *
   * @param f function to be called on failure.
   */
  def onFailure(f: PartialFunction[Throwable,Any]) : Request

}

object Request {
  def apply(input: Any)(implicit ec: ExecutionContext) : Request = Impl(input, Promise[Any](),Map(),null)

  def apply(input: Any, target: Promise[Any], annotations: Map[String,Any] = Map(), mapResponse: (Any)=>Any = null)
           (implicit ec: ExecutionContext): Request = Impl(input,target,annotations,mapResponse)


  case class Impl(input: Any, target: Promise[Any], annotations: Map[String,Any], mapResponse: (Any) => Any)
                 (implicit ec: ExecutionContext) extends Request {
    @inline final override def withAnnotations(f: Map[String,Any]=>Map[String,Any]) = Request(input,target, f(annotations),mapResponse)
    @inline final override def isCompleted: Boolean = target.isCompleted
    final lazy val future: Future[Any] =
      if(mapResponse==null) target.future
      else target.future.map(mapResponse)
    @inline final override def complete(resp: Response): Unit = target.complete(resp)
    @inline final override def mapInput(fInput: (Any)=>Any) = Request(fInput(input),target,annotations,mapResponse)
    @inline final override def map(fInput: (Any) => Any)(fOutput: (Any)=>Any) = Request(fInput(input),target,annotations,
      if(mapResponse==null) fOutput else mapResponse andThen fOutput)
    @inline final override def onComplete(f: PartialFunction[Try[Any],Any]) = {future.onComplete(f);this}
    @inline final override def onSuccess(f: PartialFunction[Any,Any]) = {future.onSuccess(f);this}
    @inline final override def onFailure(f: PartialFunction[Throwable,Any]) = {future.onFailure(f);this}
  }

  object NullRequest extends Request {
    @inline override final def input: Any = None
    @inline override final def annotations = Map.empty[String,Any]
    @inline override final def withAnnotations(f: Map[String,Any]=>Map[String,Any]) = throw new RuntimeException("Cannot annotate NullRequest")
    @inline override final def map(fInput: (Any) => Any)(fOutput: (Any)=>Any): Request = throw new RuntimeException("Cannot map NullRequest")
    @inline override final def isCompleted: Boolean = false
    @inline override final def future: Future[Any] = throw new RuntimeException(s"No future for NullRequest")
    @inline override final def complete(resp: Response): Unit = throw new RuntimeException(s"Cannot complete NullRequest")
    @inline override final def onComplete(f: PartialFunction[Try[Any],Any]) = throw new RuntimeException("NullRequest will not complete")
    @inline override final def onSuccess(f: PartialFunction[Any,Any]) = throw new RuntimeException("NullRequest will not complete")
    @inline override final def onFailure(f: PartialFunction[Throwable,Any]) = throw new RuntimeException("NullRequest will not complete")
  }

  case class Proxy(req: Request, next: ServiceRef) extends Request {
    @inline override final def input: Any = req.input
    @inline override final def withAnnotations(f: (Map[String, Any]) => Map[String, Any]): Request = Proxy(req.withAnnotations(f),next)
    @inline override final def annotations: Map[String, Any] = req.annotations
    @inline override final def onComplete(f: PartialFunction[Try[Any], Any]): Request = req.onComplete(f)
    @inline override final def onFailure(f: PartialFunction[Throwable, Any]): Request = req.onFailure(f)
    @inline override final def onSuccess(f: PartialFunction[Any, Any]): Request = req.onSuccess(f)
    @inline override final def map(fInput: (Any) => Any)(fOutput: (Any) => Any): Request = Proxy(req.map(fInput)(fOutput),next)
    @inline override final def isCompleted: Boolean = req.isCompleted
    @inline override final def future: Future[Any] = req.future
    @inline override final def complete(resp: Response): Unit = resp match {
      case Success(msg) => next ! req.withInput(msg)
      case Failure(ex) => req.failure(ex)
    }
  }

}

