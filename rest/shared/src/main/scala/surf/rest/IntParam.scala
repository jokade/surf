//     Project: surf (https://github.com/jokade/surf)
//      Module:
// Description:

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest

//import RESTAction._
//
//object IntParam {
//  def unapply(p: Any) : Option[Int] = p match {
//    case i: Int => Some(i)
//    case i: Array[Int] => i.headOption
//    case s: Array[String] => s.headOption.flatMap{ s =>
//      try { Some( s.toInt ) }
//      catch { case _:Exception => None }
//    }
//    case s: String => try{
//      Some( s.toInt )
//    } catch { case _:Exception => None }
//    case _ => None
//  }
//  def unapply(p: Option[Any]) : Option[Int] = p flatMap( unapply(_) )
//
//  /**
//   * Returns the first request parameter with the specified name from the provided parameters map, or None.
//   *
//   * @param params request parameters
//   * @param name the name of the request parameter to be returned
//   * @return request parameter value, or None, if the parameter does not exist, or is not a Int.
//   */
//  def apply(params: Params, name: String) : Option[Int] = params.get(name).map {
//    case s: String => s
//    case a: Array[_] => a.asInstanceOf[Array[String]].head
//  } flatMap (s => try{ Some(s.toInt) } catch { case _:Exception => None })
//
//  def apply(params: Params, name: String, default: =>Int) : Int = apply(params,name).getOrElse(default)
//}
