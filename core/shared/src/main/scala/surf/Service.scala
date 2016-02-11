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
  def handleException(ex: Throwable) : Unit = Service.exceptionHandler(ex)

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
  type Processor = PartialFunction[Any,Unit]

  private var _exceptionHandler: PartialFunction[Throwable,Any] = {
    case ex: Throwable => ex.printStackTrace(Console.err)
  }

  /**
   * Returns the global exception handler for [[Service]]S.
   * The default handler simply prints the stack trace to stderr.
   */
  def exceptionHandler: PartialFunction[Throwable,Any] = _exceptionHandler

  def exceptionHandler_=(f: PartialFunction[Throwable,Any]) = this.synchronized( _exceptionHandler = f )

  /**
   * Base trait for all objects that can process [[Request]]S
   */
  trait MessageProcessor {

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
