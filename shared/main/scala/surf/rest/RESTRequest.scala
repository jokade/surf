//     Project:
//      Module:
// Description:
package surf.rest

import surf.{CompleterFactory, Request}

object RESTRequest {

  def GETRequest(resource: RESTResource,
                 params: Map[String,String] = Map.empty)
                (implicit cf: CompleterFactory) : Request = Request(GET(resource,params))

  def PUTRequest(resource: RESTResource,
                 params: Map[String,String] = Map.empty,
                 body: String = "")
                (implicit cf: CompleterFactory) : Request = Request(PUT(resource,params,body))

  def POSTRequest(resource: RESTResource,
                  params: Map[String,String] = Map.empty,
                  body: String = "")
                 (implicit cf: CompleterFactory) : Request = Request(POST(resource,params,body))

  def DELETERequest(resource: RESTResource,
                    params: Map[String,String] = Map.empty)
                   (implicit cf: CompleterFactory) : Request = Request(DELETE(resource,params))
}
