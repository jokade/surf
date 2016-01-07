// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Defines the interface for REST resources
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

//import surf.{ServiceProps, ServiceRefFactory, ServiceRef}
//import surf.rest.RESTAction._
//
///**
// * Describes a REST resource.
// *
// * Requests to a RESTResource are not handled by the resource directly,
// * but are handled by a handler service instead. Hence a RESTResource may represent
// * a resource that does not exist (yet).
// */
//trait RESTResource {
//
//  /**
//   * The name of this resource (ie the suffix of the URL path represented by this RESTResource)
//   */
//  def name: String
//
//  /**
//   * Returns a resource that can handle the specified path, or None if no such resource exists.
//   * The second argument of the returned tuple contains the remaining path that should be
//   * passed in the [[RESTRequest]] when calling [[handle()]]
//   *
//   * @param path relative path segments to the requested resource
//   */
//  def child(path: Path): Option[(RESTResource,Path)]
//
//  /**
//   * Handles the specified request.
//   *
//   * @param request REST request to be handled
//   */
//  def handle(request: RESTRequest): Unit
//
//
//  def canEqual(other: Any) = other.isInstanceOf[RESTResource]
//
//  override def equals(other: Any) = other match {
//    case that: RESTResource => (that canEqual this) && that.name == this.name
//    case _ => false
//  }
//
//  override def hashCode: Int = 41 + name.hashCode
//
//  override def toString = s"RESTResource($name)"
//}
//
//
//object RESTResource {
//
//  val validNames = """[a-zA-Z0-9_\-\.]+""".r.pattern
//
//  def isValidResourceName(name: String) : Boolean = name != null && validNames.matcher(name).matches()
//
//  def apply(name: String, children: RESTResource*) : RESTResource = new Static(
//    name,
//    children.map{ c =>
//      assert(isValidResourceName(c.name), s"Invalid name for RESTResource: ${c.name}")
//      (c.name,c)
//    }.toMap)
//
//  def apply(name: String, service: =>RESTService)(implicit f: ServiceRefFactory) : RESTResource =
//    new RESTServiceResource(name,f.serviceOf(ServiceProps(service)))
//
//  class Static(val name: String, children: Map[String,RESTResource]) extends RESTResource {
//    assert( isValidResourceName(name), s"Invalid name for RESTResource: $name" )
//
//    override def handle(request: RESTRequest): Unit = request.input match {
//      case GET(_,_) => request ! RESTResponse.NoContent
//      case _ => request ! RESTResponse.MethodNotAllowed
//    }
//
//    override def child(path: Path): Option[(RESTResource,Path)] =
//      if(path.isEmpty) Some((this,Nil))
//      else children.get(path.head).flatMap(_.child(path.tail))
//  }
//
//
//  class RESTServiceResource(val name: String, service: ServiceRef) extends RESTResource {
//
//    final override def child(path: Path): Option[(RESTResource, Path)] = Some((this,path))
//
//    final override def handle(request: RESTRequest): Unit = service ! request
//  }
//}

