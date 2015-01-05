// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: CompleterFactoryS are used to create CompletableS
//
// Copyright (c) 2015 Johannes Kastner <jkspam@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import surf.Completable.Response

import scala.concurrent.{Future, Promise, ExecutionContext}

trait CompleterFactory {
  import CompleterFactory._

  /**
   * Creates a new request (flow) completable
   */
  def createCompleter() : Completer

  /**
   * Returns the ExecutionContext used by / together with this CompleterFactory
   */
  implicit def executionContext: ExecutionContext
}

object CompleterFactory {
  type Completer = Completable

  object Implicits {
    implicit val globalCF = PromiseCompleterFactory
  }
  class PromiseCompleter(implicit ec: ExecutionContext) extends Completable {
    private val _promise = Promise[Any]()

    override def isCompleted: Boolean = _promise.isCompleted
    override def future: Future[Any] = _promise.future
    override def complete(resp: Response): Unit = _promise.complete(resp)
    //override def onComplete(f: PartialFunction[Response, Unit]): Unit = future.onComplete(f)
  }

  object PromiseCompleterFactory extends CompleterFactory {
    implicit val executionContext = scala.concurrent.ExecutionContext.global
    override def createCompleter(): Completer = new PromiseCompleter
  }

}
