//     Project: surf (https://github.com/jokade/surf)
//      Module:
// Description:

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest
//
//import RESTAction._
//
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
  @inline
  def unapply(p: Option[Any]) : Option[Boolean]  = p flatMap(unapply(_))

  /**
   * Returns the first request parameter with the specified name from the provided parameters map, or None.
   *
   * @param params request parameters
   * @param name the name of the request parameter to be returned
   * @return request parameter value, or None, if the parameter does not exist, or is not a boolean.
   */
  def apply(params: Params, name: String) : Option[Boolean] =
    try {
      params.get(name).map {
        case b: Boolean => b
        case s: String => s.toBoolean
        case a: Array[_] => a.asInstanceOf[Array[String]].head.toBoolean
      }
    } catch { case _:Exception => None }

  @inline
  def apply(name: String)(implicit act: RESTAction) : Option[Boolean] = apply(act.params,name)

  /**
   * Returns the first request parameter with the specified name, or the default value.
   *
   * @param params request parameters
   * @param name the name of the request parameter to be returned
   * @param default Default value to be used if the parameter is not set in the provided Params map.
   * @return request parameter value, or None, if the parameter does not exist, or is not a boolean.
   */
  @inline
  def apply(params: Params, name: String, default: =>Boolean) : Boolean = apply(params,name).getOrElse(default)

  @inline
  def apply(name: String, default: =>Boolean)(implicit act: RESTAction) : Boolean = apply(act.params,name,default)
}

