// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines the response message types for RESTRequestS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import java.io.{OutputStream, Writer}

/**
 * Response to a REST request (ie a request with a [[RESTAction]] message).
 */
sealed trait RESTResponse

/**
 * RESTResponse types
 */
object RESTResponse {
  type ResponseGenerator = Either[(Writer)=>Unit,(OutputStream)=>Unit]

  /**
   * Response to a successful request (HTTP Code: 200)
   *
   * @param writeResponse called with the writer to which the response data should be written
   */
  case class OK(writeResponse: ResponseGenerator, ctype: String) extends RESTResponse
  object OK {
    def apply(write: (Writer)=>Unit, ctype: String): OK = OK( Left(write), ctype )
    def binary(out: (OutputStream)=>Unit, ctype: String): OK = OK( Right(out), ctype )

    /**
     * @param data response data (written using its `toString` method)
     */
    def apply(data: String, ctype: String = RESTContentType.JSON): OK = OK( (w:Writer) => w.write(data.toString), ctype )
  }

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


object RESTContentType {
  val JSON = "application/json"
  val HTML = "text/html"
  val PLAIN = "text/plain"
  val CALENDAR = "text/calendar"
}
