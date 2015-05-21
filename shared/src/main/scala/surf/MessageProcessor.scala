// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: MessageProcessor is the base trait for all Request processors
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

/**
 * Base trait for all objects that can process [[Request]]S
 */
trait MessageProcessor {
  import MessageProcessor._

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

object MessageProcessor {
  type Processor = PartialFunction[Any,Unit]

  case class CannotProcessFlowException(processor: Any, flow: Any) extends RuntimeException("Cannot process flow")

}