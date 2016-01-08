// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Base class for REST services
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import surf.Service
import surf.rest.RESTResponse.NotFound

/**
 * Base class for REST services.
 */
abstract class RESTService extends Service {

  def handle: RESTHandler

  def otherMessage(msg: Any): Unit =
    if(isRequest) ???
    else {}

  override def process = {
    case r: RESTAction => handle.applyOrElse(r, (_:RESTAction) => request ! NotFound )
    case x => otherMessage(x)
  }
}


