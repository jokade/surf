//     Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: ServiceRef implementation that executes all messages in a Future.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.service

import java.util.concurrent.ConcurrentLinkedQueue

import surf.Request.NullRequest
import surf.{Request, Service, ServiceRef}

/**
 * This [[surf.ServiceRef]] implementation puts all messages in a queue,
 * and uses the specified dispatcher to handle the queued messages with a single service instance.
 *
 * @param service Service instance by which all messages are handled
 * @param dispatcher Dispatcher used to process all enqueued messages
 */
final class QueueServiceRef(service: Service, dispatcher: Dispatcher) extends ServiceRef {
  service.self = this
  private val queue = new ConcurrentLinkedQueue[Dispatcher.Task]()

  private def enqueue(req: Request, input: Any) = {
    val task = (req,input)
    if(queue.isEmpty) {
      queue.add(task)
      dispatcher.dispatch(queue,service)
    }
    else
      queue.add(task)
  }

  override def !(req: Request): Request = {enqueue(req,req.input);req}
  override def !(msg: Any): Unit = enqueue(NullRequest,msg)
}
