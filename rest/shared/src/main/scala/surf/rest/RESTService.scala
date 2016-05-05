// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Base class for REST services
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import surf.Service
import surf.rest.RESTResponse.NotFound
import surf.rest.dsl.RequestProvider

/**
 * Base class for REST services.
 */
abstract class RESTService extends Service with RequestProvider {

  def handle: RESTHandler


  def otherMessage(msg: Any): Unit =
    if(isRequest) ???
    else {}

  implicit def requestProvider: RequestProvider = this

  override def process = {
    case r: RESTAction => handle.applyOrElse(r, (_:RESTAction) => request ! NotFound )
    case x => otherMessage(x)
  }

}

object RESTService {
  def apply(handler: RESTHandler): RESTService = new RESTServiceImpl(handler)

  class RESTServiceImpl(val handle: RESTHandler) extends RESTService
}
