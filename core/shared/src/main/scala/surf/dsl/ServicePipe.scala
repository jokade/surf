//     Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: Trait and implementations for the service pipeline DSL

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.dsl

import surf.{Request, ServiceRef}

sealed trait ServicePipe extends ServiceRef {
  def ::(head: ServiceRef) : ServicePipe = PipeCons(head,this)
}

case object PipeEnd extends ServicePipe {
  override def !(req: Request) = req
  override def !(msg: Any): Unit = {}
}

case class PipeCons(head: ServiceRef, tail: ServicePipe) extends ServicePipe {
  override def !(msg: Any) : Unit = head ! msg
  override def !(req: Request) = tail match {
    case PipeEnd => req >> head
   /* case PipeCons(Annotate(pf),tail) =>
      if(pf.isDefinedAt(req.input))
        req.withAnnotations(as => as ++ pf(req.input)) >> tail
      else
        req >> tail*/
    case next: PipeCons =>
      head ! Request.Proxy(req,next)
  }
}

