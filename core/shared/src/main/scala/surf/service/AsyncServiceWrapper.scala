//     Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: ServiceRef implementation that executes all calls asynchronously using plattform.invokeLater()

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.service

import surf.Request.NullRequest
import surf.service.AsyncServiceWrapper.ServiceRunnable
import surf.{Request, Service, ServiceRef}

final class AsyncServiceWrapper(processor: Service) extends ServiceRef {
  @inline
  override def !(req: Request): Request = {
    surf.plattform.invokeLater(new ServiceRunnable(processor,req,req.input))
    req
  }

  @inline
  override def !(msg: Any): Unit = surf.plattform.invokeLater(new ServiceRunnable(processor,NullRequest,msg))
}

object AsyncServiceWrapper {
  class ServiceRunnable(processor: Service, req: Request, msg: Any) extends Runnable {
    override def run(): Unit = processor.synchronized{
      processor.handle(req,msg)
    }
  }
}
