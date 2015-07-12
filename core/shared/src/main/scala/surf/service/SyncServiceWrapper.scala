//     Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: ServiceRef implementation that executes all calls synchronously on a wrapped Service instance

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.service

import surf.Request.NullRequest
import surf.{Request, ServiceRef, Service}

/**
 * This ServiceRef implementation wraps a Service instance and executes all
 * messages and requests sent to it synchronously on the thread from which the message/request
 * was sent.
 *
 * @param processor
 */
class SyncServiceWrapper(processor: Service) extends ServiceRef {
  processor.self = this
  override def !(req: Request): Request = this.synchronized{
    processor.handle(req,req.input)
    req
  }
  override def !(msg: Any) : Unit = this.synchronized{
    processor.handle(NullRequest,msg)
  }
}
