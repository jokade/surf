// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines the response message types for RESTRequestS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

/**
 * Response to a REST request (ie a request with a [[RESTAction]] message).
 */
sealed trait RESTResponse

/**
 * RESTResponse types
 */
object RESTResponse {

  /**
   * Response to a successful request (HTTP Code: 200)
   *
   * @param data the response data
   */
  case class OK(data: Any, ctype: RESTContentType.Value = RESTContentType.JSON) extends RESTResponse

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
   * Generic internal server error (HTTP Code: 500)
   */
  case class Error(msg: String) extends RESTResponse

  /**
   * Response to a request with unsupported RESTVerb (HTTP Code: 405)
   */
  case object MethodNotAllowed extends RESTResponse
}


object RESTContentType extends Enumeration {
  val JSON = Value
  val HTML = Value
  val PLAIN = Value
}
