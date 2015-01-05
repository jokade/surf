//     Project:
//      Module:
// Description:
package surf.service

import surf.Request.NullRequest
import surf.{Service, MessageProcessor, Request, ServiceRef}

abstract class StaticService extends MessageProcessor with ServiceRef {
  private var _req : Request = NullRequest
  override def request = _req
  override def isRequest = _req != NullRequest
  override def !(msg: Any) : Unit = handle(NullRequest,msg)
  override def !(req: Request) : Request = {handle(req,req.input); req}
  private def handle(req: Request,data:Any) = this.synchronized {
    _req = req
    try {
      process.apply(data)
    }
    catch {
      case ex:Throwable => req.failure(ex)
    }
  }
}

class SyncServiceWrapper(processor: Service) extends ServiceRef {
  override def !(req: Request): Request = this.synchronized{
    processor.handle(req,req.input)
    req
  }
  override def !(msg: Any) : Unit = this.synchronized{
    processor.handle(NullRequest,msg)
  }
}


