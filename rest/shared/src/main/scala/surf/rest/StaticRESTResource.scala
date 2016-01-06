// -   Project: surf (https://github.com/jokade/surf)
//      Module: jvm
// Description: Provides a class and corresponding handler for REST resources with static children
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import surf.rest.RESTResponse.OK
import surf.{ServiceProps, ServiceRef, ServiceRefFactory}
/*
class StaticRESTResource private(override val name: String,
                         val staticChildren: Map[String,RESTResource],
                         service: ServiceRef,
                         props: (StaticRESTResource)=>ServiceProps) extends RESTResource {
  if( !RESTResource.isValidResourceName(name) )
    throw new IllegalArgumentException(s"Invalid resource name '$name' for StaticRESTResource")

  private var _handler : ServiceRef = service

  override def handler(implicit factory: ServiceRefFactory) : ServiceRef = {
    if(_handler==null) this.synchronized{
      if(_handler==null) {
        if(props==null)
          throw new RuntimeException(s"Cannot create ServiceRef for StaticRESTResource '$name': parameter 'props' is null")
        _handler = factory.serviceOf(props(this))
      }
    }
    _handler
  }

  def dynamicChild(path: Seq[String]) : Option[RESTResource] = None

  override def child(path: List[String]): Option[RESTResource] =
    if(path.isEmpty) None
    else dynamicChild(path) orElse {
      val tail = path.tail
      if(tail.isEmpty) staticChildren.get(path.head)
      else staticChildren.get(path.head).flatMap( _.child(path.tail) )
    }

}

object StaticRESTResource {

  def apply(name: String, handler: ServiceRef, staticChildren: RESTResource*) : StaticRESTResource =
    apply(name,handler,staticChildren)

  def apply(name: String, handler: ServiceRef, staticChildren: Iterable[RESTResource]) : StaticRESTResource =
    new StaticRESTResource(name,staticChildren.map( p => (p.name,p)).toMap, handler, null)

  def apply(name: String, props: ServiceProps, staticChildren: Iterable[RESTResource]) : StaticRESTResource =
    apply(name, _ => props, staticChildren)

  def apply(name: String, props: ServiceProps, staticChildren: RESTResource*) : StaticRESTResource =
    apply(name,props,staticChildren)


  def apply(name: String, createProps: (StaticRESTResource)=>ServiceProps, staticChildren: Iterable[RESTResource]) : StaticRESTResource =
    new StaticRESTResource(name, staticChildren.map( p => (p.name,p) ).toMap, null, createProps )

  def apply(name: String, staticChildren: Iterable[RESTResource]) : StaticRESTResource =
    apply(name, res => StaticRESTResourceHandler.props(res), staticChildren)

  def apply(name: String, staticChildren: RESTResource*) : StaticRESTResource =
    apply(name,staticChildren)

  class StaticRESTResourceHandler(resource: StaticRESTResource) extends RESTService {
    import upickle._

    override def handleGET(res: RESTResource, params: Map[String,Array[String]]) = {
      val r = ResourceList( resource.staticChildren.keys )
      request ! OK( write(r) )
    }

    case class ResourceList(resources: Iterable[String])
  }

  object StaticRESTResourceHandler {
    def props(resource: StaticRESTResource) : ServiceProps = ServiceProps(new StaticRESTResourceHandler(resource))
  }
}
*/