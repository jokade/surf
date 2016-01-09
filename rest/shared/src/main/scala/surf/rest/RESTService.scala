// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Base class for REST services
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import surf.Service
import surf.rest.RESTResponse.NotFound
import surf.rest.dsl.NotFoundHandler

import scala.collection.mutable

/**
 * Base class for REST services.
 */
abstract class RESTService extends Service with NotFoundHandler {

  def handle: RESTHandler


  def otherMessage(msg: Any): Unit =
    if(isRequest) ???
    else {}

  implicit val notFoundHandler: NotFoundHandler = this

  override def process = {
    case r: RESTAction => handle.applyOrElse(r, notFound )
    case x => otherMessage(x)
  }

  override def notFound(act: RESTAction): Unit = request ! NotFound
}


