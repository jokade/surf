// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Factory for REST RequestS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import surf.{CompleterFactory, Request}

/**
 * Factory for REST RequestS
 */
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
