// -   Project: surf (https://github.com/jokade/surf)
//      Module: nodejs
// Description: Implementation of ServiceRef that wraps a Node.js Web Worker
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.js

import surf.{Request, Service, ServiceRef}

class WorkerServiceRef(service: Service) extends ServiceRef {

  /*
  val worker = WorkerFactory.createWorker{ self:Worker =>

    println("SUPPI")
    self.terminate()
  }
  */

  override def !(req: Request): Request = ???

  override def !(msg: Any): Unit = ??? //worker.postMessage(msg)
}

