//     Project:
//      Module:
// Description:
package surf.rest

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
