// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: ServiceRef is a handle to a MessageProcessor
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
// Description:
package surf

/**
 * A reference to a [[MessageProcessor]] that can handle [[Request]]S and command messages.
 */
trait ServiceRef {

  /**
   * Send a request to the service represented by this ServiceRef.
   *
   * @param req request to be handled by this service
   *
   * @return the provided Request
   */
  def !(req: Request) : Request

  /**
   * Send a command message to the service represented by this ServiceRef.
   *
   * @param msg request to be handled by service
   */
  def !(msg: Any) : Unit

}

