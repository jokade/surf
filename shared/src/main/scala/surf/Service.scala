// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Base class for surf services
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import surf.Request.NullRequest

/**
 * Base class for SuRF ServiceS.
 */
abstract class Service extends MessageProcessor {
  private var _req : Request = NullRequest
  def handleException(ex: Throwable) : Unit = {}
  final override def request : Request = _req
  final override def isRequest : Boolean = _req != NullRequest
  final def handle(req: Request, data: Any): Unit = {
    _req = req
    try {
      process.apply(data)
    }
    catch {
      case ex: Throwable =>
        handleException(ex)
        req.failure(ex)
    }
  }
}

