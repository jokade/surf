// -   Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: Defines the Request trait and default implementations
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included LICENSE file)
package surf

import surf.Completable.Response

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
 * A completable request that provides access to the corresponding response in a future.
 */
trait Request extends Completable {

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
  final def !(response: Any) : Unit = success(response)

  /**
   * Sends the current request to the specified service.
   *
   * @param service Service to which this request is sent for processing
   *
   * @return the response (request object returned by the service).
   */
  final def >>(service: ServiceRef) : Request = {
    service ! this
  }

  def mapInput(fInput: (Any)=>Any) : Request = map(fInput)( x=>x )

  /**
   * Creates an updated request from the current one, where [[input]] is replaced
   * with the specified value.
   *
   * @param input value for [[input]] in updated request
   * @return Updated reuqest
   */
  def withInput(input: Any) : Request = mapInput( _ => input)

  def mapOutput(fOutput: (Any)=>Any) : Request = map( x=>x )(fOutput)

  /**
   * Returns a new request with the same completion target as the current request,
   * but the original input is transformed by ```fInput```, and the response is transformed by ```fOutput```.
   *
   * @param fInput
   * @param fOutput
   */
  def map(fInput: (Any) => Any)(fOutput: (Any) => Any) : Request

  override def onComplete(f: PartialFunction[Try[Any],Any]) : Request
  override def onSuccess(f: PartialFunction[Any,Any]) : Request
  override def onFailure(f: PartialFunction[Throwable,Any]) : Request
}

object Request {
  def apply(input: Any)(implicit cf: CompletableFactory) : Request = Impl(input, cf.createCompletable(),Map(),null)

  def apply(input: Any, target: Completable, annotations: Map[String,Any] = Map(), mapResponse: (Any)=>Any = null) : Request =
    Impl(input,target,annotations,mapResponse)

  case class Impl(input: Any, target: Completable, annotations: Map[String,Any], mapResponse: (Any) => Any) extends Request {
    final override def withAnnotations(f: Map[String,Any]=>Map[String,Any]) = Request(input,target, f(annotations),mapResponse)
    final override def isCompleted: Boolean = target.isCompleted
    final override def future: Future[Any] = target.future
    final override def complete(resp: Response): Unit =
      if(mapResponse==null)
        target.complete(resp)
      else
        target.complete(resp.map(mapResponse))
    final override def mapInput(fInput: (Any)=>Any) = Request(fInput(input),target,annotations,mapResponse)
    final override def map(fInput: (Any) => Any)(fOutput: (Any)=>Any) = Request(fInput(input),target,annotations,
      if(mapResponse==null) fOutput else fOutput andThen mapResponse)
    final override def onComplete(f: PartialFunction[Try[Any],Any]) = {target.onComplete(f);this}
    final override def onSuccess(f: PartialFunction[Any,Any]) = {target.onSuccess(f);this}
    final override def onFailure(f: PartialFunction[Throwable,Any]) = {target.onFailure(f);this}
  }

  object NullRequest extends Request {
    override final def input: Any = None
    override final def annotations = Map.empty[String,Any]
    override final def withAnnotations(f: Map[String,Any]=>Map[String,Any]) = throw new RuntimeException("Cannot annotate NullRequest")
    override final def map(fInput: (Any) => Any)(fOutput: (Any)=>Any): Request = throw new RuntimeException("Cannot map NullRequest")
    override final def isCompleted: Boolean = false
    override final def future: Future[Any] = throw new RuntimeException(s"No future for NullRequest")
    override final def complete(resp: Response): Unit = throw new RuntimeException(s"Cannot complete NullRequest")
    override final def onComplete(f: PartialFunction[Try[Any],Any]) = throw new RuntimeException("NullRequest will not complete")
    override final def onSuccess(f: PartialFunction[Any,Any]) = throw new RuntimeException("NullRequest will not complete")
    override final def onFailure(f: PartialFunction[Throwable,Any]) = throw new RuntimeException("NullRequest will not complete")
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

