//     Project:
//      Module:
// Description:
package surf.akka

import akka.actor.Actor
import surf.Request.NullRequest
import surf.{Request, Service, MessageProcessor}

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

  /*private def handle : MessageProcessor.Processor = process orElse {
    case _ => request.failure( MessageProcessor.CannotProcessFlowException(self,request) )
  }*/
}

class ServiceWrapperActor(service: Service) extends Actor {
  final override def receive = {
    case req: Request => service.handle(req,req.input)
    case msg => service.handle(NullRequest,msg)
  }
}
