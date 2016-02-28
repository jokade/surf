//     Project: surf (https://github.com/jokade/surf)
//      Module: tests
// Description: Utility functions for executing concurrent tasks.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test

import java.util.concurrent.Callable

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

trait TaskTools {
  implicit def executionContext: ExecutionContext

  def runTasks(tasks: (()=>Unit)*) : Future[Unit] =
    Future.reduce( tasks.map( t => Future(t()) ) )((_,_)=>())

  def runTasks(n: Int)(task: =>Unit) : Future[Unit] =
    Future.reduce( (1 to n).map( _ => Future(task) ) )((_,_)=>())

  def time[T](body: =>Future[T])(processTime: Long=>Any): Future[T] = {
    val tstart = System.nanoTime()
    val res = body
    res.andThen{
      case _ => processTime(System.nanoTime() - tstart)
    }
  }

  private class Task(f: ()=>Any) extends Callable[Unit] {
    override def call(): Unit = f()
  }
}
