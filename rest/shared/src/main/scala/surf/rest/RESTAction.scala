// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Defines the standard HTTP REST request methods GET, PUT, POST and DELETE
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import RESTAction._

/**
 * A REST request message
 */
sealed trait RESTAction {

  /**
   * The resource on which this action is called.
   */
  def path: Path

  /**
   * The URL query parameters sent with this request
   */
  def params: Params

}

object RESTAction {
  type Path = Seq[String]
  type Params = Map[String,Any]
  type Body = String

  case class GET(path: Path, params: Params) extends RESTAction

  case class PUT(path: Path, params: Params, body: Body) extends RESTAction

  case class POST(path: Path, params: Params, body: Body) extends RESTAction

  case class DELETE(path: Path, params: Params) extends RESTAction
}


