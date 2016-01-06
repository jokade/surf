//     Project: surf (https://github.com/jokade/surf)
//      Module:
// Description:

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest
//
//import RESTAction._
//
//object BooleanParam {
//  def unapply(p: Any) : Option[Boolean] = p match {
//    case b: Boolean => Some(b)
//    case b: Array[Boolean] => b.headOption
//    case s: Array[String] => s.headOption.flatMap{ s =>
//      try { Some( s.toBoolean ) }
//      catch { case _:Exception => None }
//    }
//    case s: String => try{
//      Some( s.toBoolean )
//    } catch { case _:Exception => None }
//    case _ => None
//  }
//  def unapply(p: Option[Any]) : Option[Boolean]  = p flatMap(unapply(_))
//
//  /**
//   * Returns the first request parameter with the specified name from the provided parameters map, or None.
//   *
//   * @param params request parameters
//   * @param name the name of the request parameter to be returned
//   * @return request parameter value, or None, if the parameter does not exist, or is not a boolean.
//   */
//  def apply(params: Params, name: String) : Option[Boolean] = params.get(name).map {
//    case s: String => s
//    case a: Array[_] => a.asInstanceOf[Array[String]].head
//  } flatMap ( s => try{ Some(s.toBoolean) } catch { case _:Exception => None } )
//
//  def apply(params: Params, name: String, default: =>Boolean) : Boolean = apply(params,name).getOrElse(default)
//}

