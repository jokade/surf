// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines the standard HTTP REST request methods GET, PUT, POST and DELETE
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

/**
 * A REST request message
 */
sealed trait RESTAction {

  /**
   * The resource on which this action is called.
   */
  def resource: RESTResource

  /**
   * The URL query parameters sent with this request
   */
  def params: Map[String,String]

}

case class GET(resource: RESTResource, params: Map[String,String]) extends RESTAction

case class PUT(resource: RESTResource, params: Map[String,String], body: String) extends RESTAction

case class POST(resource: RESTResource, params: Map[String,String], body: String) extends RESTAction

case class DELETE(resource: RESTResource, params: Map[String,String]) extends RESTAction
