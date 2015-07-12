// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest
// Description: Provides marshallers for RESTResponseS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.akka.rest

import akka.http.scaladsl.model.{ContentType, HttpEntity, MediaType}

object RESTResponseMarshaller {
  import akka.http.scaladsl.marshalling.ToEntityMarshaller
  import akka.http.scaladsl.marshalling.Marshaller
  val `text/html` = MediaType.custom("text/html",MediaType.Encoding.Open)
  val `application/json` = MediaType.custom("application/json",MediaType.Encoding.Open)

  private val jsonType = ContentType(`application/json`)

  implicit val RESTResponseContentMarshaller : ToEntityMarshaller[RESTResponse] =
    Marshaller.withOpenCharset(`application/json`) {
      case (OK(data,RESTContentType.JSON),_) => HttpEntity(jsonType,data.toString)
      case _ => ???
    }
}
