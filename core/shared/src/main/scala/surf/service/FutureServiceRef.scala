//     Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: ServiceRef implementation that executes all messages in a Future.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.service

import java.util.concurrent.ConcurrentLinkedQueue

import surf.Request.NullRequest
import surf.{Request, Service, ServiceRef}

import scala.concurrent.{Future, ExecutionContext}

/**
 * This [[surf.ServiceRef]] implementation executes all messages and requests
 * sent to it asynchronously in a [[scala.concurrent.Future]], using the specific ExecutionContext.
 *
 * @param processor
 * @param ec ExecutionContext used to execute messages
 */
final class FutureServiceRef(processor: Service)(implicit ec: ExecutionContext) extends ServiceRef {
  type Task = (Request,Any)
  processor.self = this
  private val queue = new ConcurrentLinkedQueue[Task]()

  private def execute(): Unit = Future(processor.synchronized{
      val task = queue.remove()
      if(!queue.isEmpty)
        execute()
      processor.handle(task._1,task._2)
    })

  private def enqueue(req: Request, input: Any) = {
    val task = (req,input)
    if(queue.isEmpty) {
      queue.add(task)
      execute()
    }
    else
      queue.add(task)
  }

  override def !(req: Request): Request = {enqueue(req,req.input);req}
  override def !(msg: Any): Unit = enqueue(NullRequest,msg)
}
