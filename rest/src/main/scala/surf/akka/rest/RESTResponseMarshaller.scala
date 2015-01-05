// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest
// Description: Provides marshallers for RESTResponseS
//
// Copyright (c) 2015 Johannes Kastner <jkspam@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.akka.rest

import akka.http.model.{ContentType, HttpEntity, MediaType}
import surf.rest.{RESTContentType, RESTResponse}
import surf.rest.RESTResponse.OK

object RESTResponseMarshaller {
  import akka.http.marshalling.ToEntityMarshaller
  import akka.http.marshalling.Marshaller
  val `text/html` = MediaType.custom("text/html")
  val `application/json` = MediaType.custom("application/json")

  private val jsonType = ContentType(`application/json`)

  implicit val RESTResponseContentMarshaller : ToEntityMarshaller[RESTResponse] =
    Marshaller.withOpenCharset(`application/json`) {
      case (OK(data,RESTContentType.JSON),_) => HttpEntity(jsonType,data.toString)
      case _ => ???
    }
}
