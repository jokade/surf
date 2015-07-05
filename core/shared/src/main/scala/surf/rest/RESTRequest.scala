// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Factory for REST RequestS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import surf.{CompletableFactory, Request}

/**
 * Factory for REST RequestS
 */
object RESTRequest {
  type RequestParams = Map[String,Array[String]]

  def GETRequest(resource: RESTResource,
                 params: RequestParams = Map.empty)
                (implicit cf: CompletableFactory) : Request = Request(GET(resource,params))

  def PUTRequest(resource: RESTResource,
                 params: RequestParams = Map.empty,
                 body: String = "")
                (implicit cf: CompletableFactory) : Request = Request(PUT(resource,params,body))

  def POSTRequest(resource: RESTResource,
                  params: RequestParams = Map.empty,
                  body: String = "")
                 (implicit cf: CompletableFactory) : Request = Request(POST(resource,params,body))

  def DELETERequest(resource: RESTResource,
                    params: RequestParams = Map.empty)
                   (implicit cf: CompletableFactory) : Request = Request(DELETE(resource,params))


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

    /**
     * Returns the first request parameter with the specified name from the provided parameters map, or None.
     *
     * @param params request parameters
     * @param name the name of the request parameter to be returned
     * @return request parameter value, or None, if the parameter does not exist, or is not a boolean.
     */
    def apply(params: RequestParams, name: String) : Option[Boolean] = params.get(name).flatMap(_.headOption)
      .flatMap( s => try{ Some(s.toBoolean) } catch { case _:Exception => None })

    def apply(params: RequestParams, name: String, default: =>Boolean) : Boolean = apply(params,name).getOrElse(default)
  }

  object StringParam {
    def unapply(p: Any) : Option[String] = p match {
      case s: String => Some(s)
      case s: Array[String] => s.headOption
      case s => Some(s.toString)
    }
    def unapply(p: Option[Any]) : Option[String] = p flatMap( unapply(_) )

    /**
     * Returns the first request parameter with the specified name from the provided parameters map, or None.
     *
     * @param params request parameters
     * @param name the name of the request parameter to be returned
     * @return request parameter value, or None, if the parameter does not exist.
     */
    def apply(params: RequestParams, name: String) : Option[String] = params.get(name).flatMap(_.headOption)

    def apply(params: RequestParams, name: String, default: =>String) : String = apply(params,name).getOrElse(default)
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

    /**
     * Returns the first request parameter with the specified name from the provided parameters map, or None.
     *
     * @param params request parameters
     * @param name the name of the request parameter to be returned
     * @return request parameter value, or None, if the parameter does not exist, or is not a Int.
     */
    def apply(params: RequestParams, name: String) : Option[Int] = params.get(name).flatMap(_.headOption)
      .flatMap( s => try{ Some(s.toInt) } catch { case _:Exception => None })

    def apply(params: RequestParams, name: String, default: =>Int) : Int = apply(params,name).getOrElse(default)
  }
}
