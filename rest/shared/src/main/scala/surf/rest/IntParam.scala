//     Project: surf (https://github.com/jokade/surf)
//      Module:
// Description:

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest

//import RESTAction._
//
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
//  def unapply(p: Option[Any]) : Option[Int] = p flatMap( unapply(_) )
//
  /**
   * Returns the first request parameter with the specified name from the provided parameters map, or None.
   *
   * @param params request parameters
   * @param name the name of the request parameter to be returned
   * @return request parameter value, or None, if the parameter does not exist, or is not a Int.
   */
  def apply(params: Params, name: String) : Option[Int] =
    try {
      params.get(name).map {
        case i: Int => i
        case s: String => s.toInt
        case a: Array[_] => a.asInstanceOf[Array[String]].head.toInt
      }
    } catch { case _:Exception => None }

    @inline
    def apply(name: String)(implicit act: RESTAction) : Option[Int] = apply(act.params,name)

    /**
     * Returns the first request parameter with the specified name, or the default value.
     *
     * @param params request parameters
     * @param name the name of the request parameter to be returned
     * @param default Default value to be used if the parameter is not set in the provided Params map.
     * @return request parameter value, or None, if the parameter does not exist, or is not a boolean.
     */
    @inline
    def apply(params: Params, name: String, default: =>Int) : Int = apply(params,name).getOrElse(default)

    @inline
    def apply(name: String, default: =>Int)(implicit act: RESTAction) : Int = apply(act.params,name,default)

}
