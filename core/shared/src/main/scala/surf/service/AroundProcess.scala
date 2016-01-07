//     Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: A service mixin that executes a function before and after the request is processed.

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.service

import surf.{Request, Service}

/**
 * A Service mixin that executes custom behaviour before and after the message is processed.
 */
trait AroundProcess extends Service {
  /**
   * Called before [[Service.process]], which will only execute if this method returns true.
   *
   * @param req request instance associated with the current message (may be a [[Request.NullRequest]]
   * @param data message data
   */
  def before(req: Request, data: Any) : Boolean = true

  def after(req: Request, data: Any) : Unit = {}

  override protected[surf] def handle(req: Request, data: Any): Unit = {
    if( before(req,data) )
      super.handle(req, data)
    after(req,data)
  }
}
