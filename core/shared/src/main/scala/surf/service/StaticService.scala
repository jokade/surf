// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Provides a base class for static singleton services
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.service

import surf.Request.NullRequest
import surf.{Service, MessageProcessor, Request, ServiceRef}

/**
 * Base class for static singleton services
 * (ie the ServiceRef contains directly the service implementation)
 */
abstract class StaticService extends MessageProcessor with ServiceRef {
  private var _req : Request = NullRequest
  override def request = _req
  override def isRequest = _req != NullRequest
  override def !(msg: Any) : Unit = handle(NullRequest,msg)
  override def !(req: Request) : Request = {handle(req,req.input); req}
  private def handle(req: Request,data:Any) = this.synchronized {
    _req = req
    try {
      process.apply(data)
    }
    catch {
      case ex:Throwable => req.failure(ex)
    }
  }
}


