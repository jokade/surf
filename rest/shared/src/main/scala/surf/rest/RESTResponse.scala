// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Defines the response message types for RESTRequestS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import java.io.{InputStream, OutputStream}

import surf.rest.RESTResponse.ContentType

/**
 * Response to a REST request (ie a request with a [[RESTAction]] message).
 */
sealed trait RESTResponse

/**
 * RESTResponse types
 */
object RESTResponse {

  type ContentType = String

  trait StringWriter {
    def write(s: String, charset: String) : Unit
  }
  object StringWriter {
    def apply(): StringWriter = new Impl
    class Impl extends StringWriter {
      val buf = new StringBuilder
      override def write(s: String, charset: String): Unit = buf ++= s
      override def toString = buf.toString()
    }
  }
  type ResponseWriter = Either[StringWriter=>Unit,OutputStream=>Unit]


  /**
   * Response to a successful request (HTTP Code: 200)
   *
   * @param writeResponse called with the writer to which the response data should be written
   */
  case class OK(writeResponse: ResponseWriter, ctype: ContentType) extends RESTResponse
  object OK {
    def apply(write: (StringWriter)=>Unit, ctype: ContentType) : OK = OK( Left(write), ctype )
    def stream(write: (OutputStream)=>Unit, ctype: ContentType) : OK = OK( Right(write), ctype )

    /**
     * Response to a successful reqeust with a string body.
     *
     * @param data response data (written using its `toString` method)
     */
    def apply(data: String, ctype: ContentType = ContentType.JSON, charset: String = Charset.UTF8): OK =
      OK((w:StringWriter) => w.write(data,charset), ctype )

  }

  /**
   * Respond to a request with the specified resource.
   *
   * @param path Path to the resource to be used as the response body (e.g. path to a HTML file)
   * @param ctype Content type
   * @param status HTTP status code (default: OK/200)
   */
  case class RespondWithResource(path: String, ctype: ContentType, status: Int = 200) extends RESTResponse

  /**
   * Respond to a request with the contents of the specified InputStream.
   *
   * @param stream InputStream from which to read the response body
   * @param ctype Content type
   * @param status HTTP status code (default: OK/200)
   */
  case class RespondWithStream(stream: InputStream, ctype: ContentType, status: Int = 200) extends RESTResponse

  /**
   * The requested resource was successfully created (HTTP Code: 201)
   *
   * @param data the response data
   * @param location URL of the newly created resource (optional)
   * @param ctype response content type
   */
  // TODO: correct semantics for CREATED?
  //case class Created(data: Any, location: Option[String] = None, ctype: RESTContentType.Value = RESTContentType.JSON) extends RESTResponse

  /**
   * Response to a successful request with empty content (HTTP Code: 204)
   */
  case object NoContent extends RESTResponse

  /**
   * Bad request / client error (HTTP Code: 400)
   */
  case class BadRequest(msg: String) extends RESTResponse

  /**
   * Resource not found (HTTP Code: 404)
   */
  case object NotFound extends RESTResponse

  /**
   * Conflict (HTTP Code: 409)
   */
  case class Conflict(msg: String) extends RESTResponse

  /**
   * Generic internal server error (HTTP Code: 500)
   */
  case class Error(msg: String) extends RESTResponse

  /**
   * Response to a request with unsupported RESTVerb (HTTP Code: 405)
   */
  case object MethodNotAllowed extends RESTResponse
}


object ContentType {
  val CALENDAR     = "text/calendar"
  val CSS          = "text/css"
  val HTML         = "text/html"
  val JAVASCRIPT   = "text/javascript"
  val JPEG         = "image/jpeg"
  val JSON         = "application/json"
  val PLAIN        = "text/plain"

  def fromSuffix(suffix: String) : Option[ContentType] = suffix match {
    case "css"        => Some(CSS)
    case "html"       => Some(HTML)
    case "jpg"|"jpeg" => Some(JPEG)
    case "js"         => Some(JAVASCRIPT)
    case "json"       => Some(JSON)
    case _            => Some(PLAIN)

  }
}

object Charset {
  val UTF8 = "utf-8"
}
