// -   Project: surf (https://github.com/jokade/surf)
//      Module: akka
// Description: Provides Akka actor-based implementations for surf message processors
//
// Copyright (c) 2015 Johannes Kastner <jkspam@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.akka

import akka.actor.Actor
import surf.Request.NullRequest
import surf.{Request, Service, MessageProcessor}

/**
 * Base class for Actors that can act as endpoints to a [[ServiceRef]]
 */
abstract class ServiceActor extends Actor with MessageProcessor {
  private var _req: Request = NullRequest

  final override def request = _req

  final override def receive : Receive = {
    case req: Request => {
      _req = req
      process(req.input)
      _req = NullRequest
    }
    case msg => process(msg)
  }

}

/**
 * An actor that executes the provided Service logic.
 *
 * @param service The service to be executed within this Actor
 */
class ServiceWrapperActor(service: Service) extends Actor {
  final override def receive = {
    case req: Request => service.handle(req,req.input)
    case msg => service.handle(NullRequest,msg)
  }
}
