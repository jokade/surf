//     Project: surf (https://github.com/jokade/surf)
//      Module:
// Description:

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest

import RESTAction._

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
  def apply(params: Params, name: String) : Option[String] = params.get(name).map {
    case s: String => s
    case a: Array[_] => a.asInstanceOf[Array[String]].head
  }

  def apply(params: Params, name: String, default: =>String) : String = apply(params,name).getOrElse(default)
}
