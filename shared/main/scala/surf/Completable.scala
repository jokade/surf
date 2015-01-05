// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: A Completable holds the response to a request
//
// Copyright (c) 2015 Johannes Kastner <jkspam@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import scala.concurrent.Future
import scala.util.{Success, Failure, Try}

object Completable {

  /**
   * Response to a request flow.
   */
  type Response = Try[Any]

  case class RequestAlreadyCompletedException(completer: Completable) extends RuntimeException(s"Request has already been completed!")

  case object EmptyCompleted extends Completable {
    val isCompleted: Boolean = true
    val future: Future[Option[Any]] = Future.successful(None)
    override def complete(resp: Response): Unit = throw RequestAlreadyCompletedException(this)
  }
}

trait Completable {
  import surf.Completable._

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
  final def failure(ex: Throwable) : Unit = complete(Failure(ex))

  /**
   * Completes the request (flow) successfully with the provided response result.
   *
   * @param result The result data object
   *
   * @throws RequestAlreadyCompletedException if the request has already been completed
   */
  final def success(result: Any) : Unit = complete(Success(result))

  /**
   * Completes the request (flow) successfully (but without an explicit result)
   *
   * @throws RequestAlreadyCompletedException if the request has already been completed
   */
  //final def success() : Unit = complete(Success(None))

  /**
   * Returns a future that will be completed when this request (flow) is completed.
   */
  def future : Future[Any]

  //def onComplete(f: PartialFunction[Response,Unit]) : Unit
}