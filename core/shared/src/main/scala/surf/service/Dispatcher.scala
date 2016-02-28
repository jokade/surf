//     Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Processes messages enqueued to a QueueServiceRef.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.service

import surf.service.Dispatcher.Queue
import surf.{Request, Service}

import scala.concurrent.{ExecutionContext, Future}

trait Dispatcher {
  def dispatch(queue: Queue, processor: Service): Unit
}

object Dispatcher {
  type Task = (Request, Any)
  type Queue = java.util.concurrent.ConcurrentLinkedQueue[Task]

  final class FutureDispatcher(ec: ExecutionContext) extends Dispatcher {
    override def dispatch(queue: Queue, processor: Service): Unit = Future(processor.synchronized{
      val task = queue.remove()
      if(!queue.isEmpty)
        dispatch(queue,processor)
      processor.handle(task._1,task._2)
    })(ec)
  }
}

