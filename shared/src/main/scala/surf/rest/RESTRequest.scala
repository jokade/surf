// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Factory for REST RequestS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import surf.{CompleterFactory, Request}

/**
 * Factory for REST RequestS
 */
object RESTRequest {

  def GETRequest(resource: RESTResource,
                 params: Map[String,Array[String]] = Map.empty)
                (implicit cf: CompleterFactory) : Request = Request(GET(resource,params))

  def PUTRequest(resource: RESTResource,
                 params: Map[String,Array[String]] = Map.empty,
                 body: String = "")
                (implicit cf: CompleterFactory) : Request = Request(PUT(resource,params,body))

  def POSTRequest(resource: RESTResource,
                  params: Map[String,Array[String]] = Map.empty,
                  body: String = "")
                 (implicit cf: CompleterFactory) : Request = Request(POST(resource,params,body))

  def DELETERequest(resource: RESTResource,
                    params: Map[String,Array[String]] = Map.empty)
                   (implicit cf: CompleterFactory) : Request = Request(DELETE(resource,params))


  object BooleanParam {
    def unapply(p: Any) : Option[Boolean] = p match {
      case b: Boolean => Some(b)
      case b: Array[Boolean] => b.headOption
      case s: Array[String] => s.headOption.flatMap{ s =>
        try { Some( s.toBoolean ) }
        catch { case _:Exception => None }
      }
      case s: String => try{
        Some( s.toBoolean )
      } catch { case _:Exception => None }
      case _ => None
    }
    def unapply(p: Option[Any]) : Option[Boolean]  = p flatMap(unapply(_))
  }

  object StringParam {
    def unapply(p: Any) : Option[String] = p match {
      case s: String => Some(s)
      case s: Array[String] => s.headOption
      case s => Some(s.toString)
    }
    def unapply(p: Option[Any]) : Option[String] = p flatMap( unapply(_) )
  }

  object IntParam {
    def unapply(p: Any) : Option[Int] = p match {
      case i: Int => Some(i)
      case i: Array[Int] => i.headOption
      case s: Array[String] => s.headOption.flatMap{ s =>
        try { Some( s.toInt ) }
        catch { case _:Exception => None }
      }
      case s: String => try{
        Some( s.toInt )
      } catch { case _:Exception => None }
      case _ => None
    }
    def unapply(p: Option[Any]) : Option[Int] = p flatMap( unapply(_) )
  }
}
