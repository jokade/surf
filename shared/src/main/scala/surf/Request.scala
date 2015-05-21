// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines the Request trait and default implementations
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import surf.Completable.Response

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

  override def onComplete(f: PartialFunction[Try[Any],Any]) : Request
  override def onSuccess(f: PartialFunction[Any,Any]) : Request
  override def onFailure(f: PartialFunction[Throwable,Any]) : Request
}

object Request {
  def apply(input: Any)(implicit cf: CompleterFactory) : Request = Impl(input, cf.createCompleter(),null)

  def apply(input: Any, target: Completable, mapResponse: (Any)=>Any = null) : Request = Impl(input,target,mapResponse)

  case class Impl(input: Any, target: Completable, mapResponse: (Any) => Any) extends Request {
    override def isCompleted: Boolean = target.isCompleted
    override def future: Future[Any] = target.future
    override def complete(resp: Response): Unit =
      if(mapResponse==null)
        target.complete(resp)
      else
        target.complete(resp.map(mapResponse))
    override def mapInput(fInput: (Any)=>Any) = Request(fInput(input),target)
    override def mapOutput(fOutput: (Any)=>Any) = Request(input,target,fOutput)
    override def map(fInput: (Any) => Any)(fOutput: (Any)=>Any) = Request(fInput(input),target,fOutput)
    override def onComplete(f: PartialFunction[Try[Any],Any]) = {target.onComplete(f);this}
    override def onSuccess(f: PartialFunction[Any,Any]) = {target.onSuccess(f);this}
    override def onFailure(f: PartialFunction[Throwable,Any]) = {target.onFailure(f);this}
  }

  object NullRequest extends Request {
    override def input: Any = None
    override def map(fInput: (Any) => Any)(fOutput: (Any)=>Any): Request = throw new RuntimeException(s"Cannot map NullRequest")
    override def isCompleted: Boolean = false
    override def future: Future[Any] = throw new RuntimeException(s"No future for NullRequest")
    override def complete(resp: Response): Unit = throw new RuntimeException(s"Cannot complete NullRequest")
    override def onComplete(f: PartialFunction[Try[Any],Any]) = throw new RuntimeException("NullRequest will not complete")
    override def onSuccess(f: PartialFunction[Any,Any]) = throw new RuntimeException("NullRequest will not complete")
    override def onFailure(f: PartialFunction[Throwable,Any]) = throw new RuntimeException("NullRequest will not complete")
  }
}

