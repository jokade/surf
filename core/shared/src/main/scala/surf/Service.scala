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
  private var _self : ServiceRef = _
  def self: ServiceRef = _self
  protected[surf] def self_=(ref: ServiceRef) = this.synchronized( _self = ref )
  def handleException(ex: Throwable) : Unit = {
    Console.err.println(ex.toString)
  }
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
        if(isRequest)
          req.failure(ex)
    }
  }
}

