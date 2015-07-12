//     Project: surf (https://github.com/jokade/surf)
//      Module: akka
// Description: ServiceRef implamentation for akka actors

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.akka

import akka.actor.{ActorRef, ActorRefFactory, ActorSelection, Props}
import surf.{Request, ServiceRef}

import scala.language.implicitConversions

trait ServiceActorRef extends ServiceRef

object ServiceActorRef {

  /**
   * Creates a ServiceActorRef that wraps an ActorRef
   *
   * @param actor
   */
  def apply(actor: ActorRef) : ServiceActorRef = ActorRefWrapper(actor)

  /**
   * Creates a ServiceActorRef that wraps an ActorSelection
   *
   * @param actor
   */
  def apply(actor: ActorSelection) : ServiceActorRef = ActorSelectionWrapper(actor)

  private case class ActorRefWrapper(ref: ActorRef) extends ServiceActorRef {
    override def !(msg: Any): Unit = ref ! msg
    override def !(req: Request): Request = {ref ! req; req}
  }

  private case class ActorSelectionWrapper(ref: ActorSelection) extends ServiceActorRef {
    override def !(msg: Any): Unit = ref ! msg
    override def !(req: Request): Request = {ref ! req; req}
  }

  /**
   * Creates a ServiceActorRef that spins up a new Actor for every sent Request.
   *
   * @param props
   * @param factory
   */
  def perRequest(props: Props)(implicit factory: ActorRefFactory) : ServiceActorRef = PerRequestActor(props,factory)

  private case class PerRequestActor(props: Props, factory: ActorRefFactory) extends ServiceActorRef {
    override def !(msg: Any): Unit = factory.actorOf(props) ! msg
    override def !(req: Request): Request = {
      factory.actorOf(props) ! req
      req
    }
  }

}