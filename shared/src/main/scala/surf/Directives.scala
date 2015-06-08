// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines common directives of the surf DSL
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import surf.Completable.Response
import surf.MessageProcessor.Processor
import surf.service.StaticService

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.language.implicitConversions
import scala.util.{Try, Failure, Success}

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
    /*def ::(head: PartialFunction[Any,FilterMapping])(implicit cf: CompleterFactory) : ServiceRef =
      new FilterService(head,this)
      */
  }

  case object PipeEnd extends PipeService {
    override def !(req: Request) = req
    override def !(msg: Any): Unit = {}
  }

  case class PipeCons(head: ServiceRef, tail: PipeService)(implicit cf: CompleterFactory) extends PipeService {
    override def !(msg: Any) : Unit = head ! msg
    override def !(req: Request) = tail match {
      case PipeEnd => req >> head
      case PipeCons(Annotate(pf),tail) =>
        if(pf.isDefinedAt(req.input))
          req.withAnnotations(as => as ++ pf(req.input)) >> tail
        else
          req >> tail
      case next: PipeCons =>
      /*{
        val wrapped = Request(req.input)
        head ! wrapped
        wrapped.future.onComplete{
          case Success(result) => next ! req.withInput(result)
          case Failure(ex) => req.failure(ex)
        }(cf.executionContext)
        req
      }*/
        head ! Request.Proxy(req,next)
    }
  }


  private var _defaultTimeout: Duration = Duration(5,"seconds")
  def defaultTimeout_=(d: Duration) = this.synchronized{ _defaultTimeout = d }
  implicit def defaultTimeout: Duration = _defaultTimeout

  def awaitMap[T](req: Request)(pf: PartialFunction[Any,T])(implicit atMost: Duration) : T =
    pf(Await.result(req.future,atMost))

  def await(req: Request)(implicit atMost: Duration) : Any = Await.result(req.future,atMost)

  object Transform {
    def apply(pf: PartialFunction[Any,Any]) : ServiceRef = new Impl(pf)

    private class Impl(pf: PartialFunction[Any,Any]) extends ServiceRef {
      override def !(req: Request): Request = {
        req ! pf(req.input)
        req
      }
      override def !(msg: Any): Unit = ???
    }
  }

  case class Annotate(pf: PartialFunction[Any,Map[String,Any]]) extends ServiceRef {
    override def !(req: Request): Request = ???
    override def !(msg: Any): Unit = ???
  }
  /*sealed trait FilterMapping
  case class Complete(value: Any) extends FilterMapping
  case class Next(value: Any) extends FilterMapping
  */
}

object Directives extends Directives

