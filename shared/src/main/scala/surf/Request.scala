// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines the Request trait and default implementations
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import surf.Completable.Response
import surf.MessageProcessor.Processor
import surf.service.StaticService

import scala.concurrent.Future
import scala.util.{Try, Success}

/**
 * A completable request that provides access to the corresponding response in a future.
 */
trait Request extends Completable {

  /**
   * The request message.
   */
  def input: Any

  def annotations: Map[String,Any]

  def withAnnotations(f: Map[String,Any]=>Map[String,Any]) : Request

  final def !(resp: Any) : Unit = success(resp)

  final def >>(service: ServiceRef) : Request = {
    service ! this
    this
  }

  def mapInput(fInput: (Any)=>Any) : Request = map(fInput)( x=>x )
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

  /**
   * Maps the request input, if `f` is defined for this input. Otherwise the
   * the current request is completed with `completeWith`.
   *
   * @param completeWith value used for completion if `f` is not defined for the input value
   * @param f partial function that maps the request input, if it is defined for this input value
   */
  //def mapOrComplete(completeWith: Any)(f: PartialFunction[Any,Any]) : Request

  override def onComplete(f: PartialFunction[Try[Any],Any]) : Request
  override def onSuccess(f: PartialFunction[Any,Any]) : Request
  override def onFailure(f: PartialFunction[Throwable,Any]) : Request
}

object Request {
  def apply(input: Any)(implicit cf: CompleterFactory) : Request = Impl(input, cf.createCompleter(),Map(),null)

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
    //final override def mapOutput(fOutput: (Any)=>Any) = Request(input,target,annotations,fOutput andThen mapResponse)
    final override def map(fInput: (Any) => Any)(fOutput: (Any)=>Any) = Request(fInput(input),target,annotations,
      if(mapResponse==null) fOutput else fOutput andThen mapResponse)
    final override def onComplete(f: PartialFunction[Try[Any],Any]) = {target.onComplete(f);this}
    final override def onSuccess(f: PartialFunction[Any,Any]) = {target.onSuccess(f);this}
    final override def onFailure(f: PartialFunction[Throwable,Any]) = {target.onFailure(f);this}
    //override def mapOrComplete(completeWith: Any)(f: PartialFunction[Any,Any]): Request = this>>(MapOrCompleteService(completeWith,f,target))
  }

  object NullRequest extends Request {
    override def input: Any = None
    override def annotations = Map.empty[String,Any]
    override def withAnnotations(f: Map[String,Any]=>Map[String,Any]) = throw new RuntimeException("Cannot annotate NullRequest")
    override def map(fInput: (Any) => Any)(fOutput: (Any)=>Any): Request = throw new RuntimeException("Cannot map NullRequest")
    override def isCompleted: Boolean = false
    override def future: Future[Any] = throw new RuntimeException(s"No future for NullRequest")
    override def complete(resp: Response): Unit = throw new RuntimeException(s"Cannot complete NullRequest")
    override def onComplete(f: PartialFunction[Try[Any],Any]) = throw new RuntimeException("NullRequest will not complete")
    override def onSuccess(f: PartialFunction[Any,Any]) = throw new RuntimeException("NullRequest will not complete")
    override def onFailure(f: PartialFunction[Throwable,Any]) = throw new RuntimeException("NullRequest will not complete")
    //override def mapOrComplete(completeWith: Any)(f: PartialFunction[Any,Any]) = throw new RuntimeException("Cannot complete NullRequest")
  }

  /*
  case class MapOrCompleteService(completeWith: Any, f: PartialFunction[Any,Any], target: Completable) extends StaticService {
    override def process = {
      case msg if f.isDefinedAt(msg) => request ! f.apply(msg)
      case _ => target.success(completeWith)
    }
  }*/
}

