// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines common directives of the surf DSL
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import scala.language.implicitConversions
import scala.util.{Failure, Success}

trait Directives {

  implicit class RequestDSL(data: Any) {
    def >>(ref: ServiceRef)(implicit cf: CompleterFactory) : Request = {
      ref ! Request(data)
    }
  }

  implicit class ServiceDSL(service: ServiceRef) {
    def ::(left: ServiceRef)(implicit cf: CompleterFactory) : PipeService = left :: service :: PipeEnd
  }

  sealed trait PipeService extends ServiceRef {
    def ::(head: ServiceRef)(implicit cf: CompleterFactory) : PipeService = PipeCons(head,this)(cf)
  }

  case object PipeEnd extends PipeService {
    override def !(req: Request) = req
    override def !(msg: Any): Unit = {}
  }

  case class PipeCons(head: ServiceRef, tail: PipeService)(implicit cf: CompleterFactory) extends PipeService {
    override def !(msg: Any) : Unit = head ! msg
    override def !(req: Request) = tail match {
      case PipeEnd => req >> head
      case next: PipeCons => {
        val wrapped = Request(req.input)
        head ! wrapped
        wrapped.future.onComplete{
          case Success(result) => next ! req.withInput(result)
          case Failure(ex) => req.failure(ex)
        }(cf.executionContext)
        req
      }
    }
  }

}

object Directives extends Directives

