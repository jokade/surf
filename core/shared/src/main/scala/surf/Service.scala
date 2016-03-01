// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Base class for surf services
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import surf.Request.NullRequest
import surf.Service.MessageProcessor

/**
 * Base class for SuRF ServiceS.
 */
abstract class Service extends MessageProcessor {
  private var _req : Request = NullRequest
  private var _self : ServiceRef = _

  /**
   * [[ServiceRef]] for this Service instance.
   */
  def self: ServiceRef = _self

  protected[surf] def self_=(ref: ServiceRef) = this.synchronized( _self = ref )

  /**
   * Called when an exception occurs while executing [[process]].
   * The default implementation delegates to [[Service.exceptionHandler]].
   *
   * @param ex
   */
  def handleException(ex: Throwable) : Unit = Service.exceptionHandler(ex,isRequest)

  implicit final override def request : Request = _req
  final override def isRequest : Boolean = _req != NullRequest
  protected[surf] def handle(req: Request, data: Any): Unit = {
    _req = req
    try {
      process.apply(data)
    }
    catch {
      case ex: Throwable =>
        handleException(ex)
        if(isRequest)
          req.failure(ex)
    }
  }
}


object Service {

  private var _exceptionHandler: Function2[Throwable,Boolean,Any] = (ex: Throwable,isRequest:Boolean) =>
    if(isRequest) {}
    else throw ex

  /**
   * Returns the global exception handler for [[Service]]S.
   * The default handler does nothing for Exceptions raised in a request,
   * but throws all exceptions raised during processing of a simple message.
   *
   * The first argument to the handler function is the raised exception,
   * the second argument indicates if the exception was raised during processing of a request.
   */
  def exceptionHandler: Function2[Throwable,Boolean,Any] = _exceptionHandler

  def exceptionHandler_=(f: Function2[Throwable,Boolean,Any]) = this.synchronized( _exceptionHandler = f )

  /**
   * Base trait for all objects that can process [[Request]]S
   */
  trait MessageProcessor {
    type Processor = PartialFunction[Any,Unit]

    /**
     * Returns the Request currently in processing (if any).
     */
    def request : Request

    /**
     * Returns true iff the currently processed message is a Request
     */
    def isRequest : Boolean

    /**
     * Processes incoming flow requests
     */
    def process: Processor
  }
}
