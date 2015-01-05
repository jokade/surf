//     Project:
//      Module:
// Description:
package surf.rest

import surf.{ServiceRefFactory, ServiceRef}

/**
 * Describes a REST resource.
 *
 * Requests to a RESTResource are not handled by the resource directly,
 * but are handled by a handler service instead. Hence a RESTResource may represent
 * a resource that does not exist (yet).
 */
trait RESTResource {

  /**
   * The name of this resource (ie the suffix of the URL path represented by this RESTResource)
   */
  def name: String

  /**
   * Returns the specified child resource, or None if the resource does not exist.
   *
   * @param path List with relative path segments to the requested resource
   */
  def child(path: List[String]) : Option[RESTResource]

  /**
   * Returns the service to be used for handling requests to this resource.
   *
   * @param factory ServiceRefFactory for creating the ServiceRef, if necessary
   */
  def handler(implicit factory: ServiceRefFactory): ServiceRef


  def canEqual(other: Any) = other.isInstanceOf[RESTResource]

  override def equals(other: Any) = other match {
    case that: RESTResource => (that canEqual this) && that.name == this.name
    case _ => false
  }

  override def hashCode: Int = 41 + name.hashCode

  override def toString = s"RESTResource($name)"
}


object RESTResource {

  val validNames = "[a-zA-Z0-9_-]+".r.pattern

  def isValidResourceName(name: String) : Boolean = name != null && validNames.matcher(name).matches()

}

object RESTPath {

  object IntNumber {
    def unapply(s: String) : Option[Int] = try{
      Some(s.toInt)
    }
    catch {
      case _:Throwable => None
    }

    def unapply(s: List[String]) : Option[Int] = s match {
      case p :: Nil => unapply(p)
      case _ => None
    }
  }

}
