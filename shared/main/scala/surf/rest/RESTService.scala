package surf.rest

import surf.Service

/**
 * Base class for REST services.
 */
abstract class RESTService extends Service {

  def handleGET(resource: RESTResource, params: Map[String,Any]) : Unit = request ! RESTResponse.MethodNotAllowed

  def handlePUT(resource: RESTResource, params: Map[String,Any], body: String) : Unit = request ! RESTResponse.MethodNotAllowed

  def handlePOST(resource: RESTResource, params: Map[String,Any], body: String) : Unit = request ! RESTResponse.MethodNotAllowed

  def handleDELETE(resource: RESTResource, params: Map[String,Any]) : Unit = request ! RESTResponse.MethodNotAllowed

  def handleOther(message: Any) : Option[Any] = ???


  final override def process = {
    case GET(res,params)       if isRequest => handleGET(res,params)
    case PUT(res,params,body)  if isRequest => handlePUT(res,params,body)
    case POST(res,params,body) if isRequest => handlePOST(res,params,body)
    case DELETE(res,params)    if isRequest => handleDELETE(res,params)
    case msg                   if isRequest => handleOther(msg).foreach( request ! _ )
    case msg                                => handleOther(msg)
  }
}
