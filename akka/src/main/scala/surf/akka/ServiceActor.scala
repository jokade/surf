//     Project: surf (https://github.com/jokade/surf)
//      Module: akka
// Description: An akka actor that executes surf ServiceS

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.akka

import akka.actor.{Props, Actor}
import surf.Request.NullRequest
import surf.{ServiceProps, Request, Service}

/**
 * An actor that executes the provided Service logic.
 *
 * @param service The service to be executed within this Actor
 */
class ServiceActor(service: Service) extends Actor {
  final override def receive = {
    case req: Request => service.handle(req,req.input)
    case msg => service.handle(NullRequest,msg)
  }
}

object ServiceActor {
  def apply(props: ServiceProps): Props = Props(new ServiceActor(props.createService()))
  def apply(props: =>Service): Props = apply(ServiceProps(props))
}
