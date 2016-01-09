// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest
// Description: Provides marshallers for RESTResponseS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest.akka

import akka.http.scaladsl.model.{ContentType, HttpEntity, MediaType}
import surf.rest.{ContentType, RESTResponse}
import surf.rest.RESTResponse.OK

object RESTResponseMarshaller {
  import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
  val `text/html` = MediaType.custom("text/html",MediaType.Encoding.Open)
  val `application/json` = MediaType.custom("application/json",MediaType.Encoding.Open)

  private val jsonType = ContentType(`application/json`)

  implicit val RESTResponseContentMarshaller : ToEntityMarshaller[RESTResponse] =
    Marshaller.withOpenCharset(`application/json`) {
      case (OK(data,ContentType.JSON),_) => HttpEntity(jsonType,data.toString)
      case _ => ???
    }
}
