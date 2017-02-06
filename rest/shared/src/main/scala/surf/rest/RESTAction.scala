// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Defines the standard HTTP REST request methods GET, PUT, POST and DELETE
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import java.util.Scanner

import surf.rest.RESTResponse.ContentType

/**
 * A REST request message
 */
sealed trait RESTAction {
  /**
   * The resource on which this action is called.
   */
  def path: Path

  /**
   * The URL query parameters sent with this request
   */
  def params: Params

  /**
   * Returns a copy of this action with an updated path.
   */
  def withPath(path: Path): RESTAction

  def withParams(param: (String,Any)*): RESTAction
}

object RESTAction {

  sealed abstract class BodyHolder extends RESTAction {
    def body: Body
    def ctype: ContentType
    def encoding: Encoding
    lazy val bodyAsString: String = {
      new Scanner(body,encoding).useDelimiter("\\A").next()
    }
  }

  case class GET(path: Path, params: Params) extends RESTAction {
    override def withPath(path: Path): GET = copy(path = path)
    override def withParams(param: (String,Any)*): GET = copy( params = params++param )
    override def toString: String = s"GET '${path.mkString("/")}'"
  }

  case class PUT(path: Path, params: Params, body: Body, ctype: ContentType, encoding: String)
    extends BodyHolder {
    override def withPath(path: Path): PUT = copy(path = path)
    override def withParams(param: (String,Any)*): PUT = copy( params = params++param )
    override def toString: String = s"PUT '${path.mkString("/")}'"
  }

  case class POST(path: Path, params: Params, body: Body, ctype: ContentType, encoding: String)
    extends BodyHolder {
    override def withPath(path: Path): POST = copy(path = path)
    override def withParams(param: (String,Any)*): POST = copy( params = params++param )
    override def toString: String = s"POST '${path.mkString("/")}'"
  }

  case class DELETE(path: Path, params: Params) extends RESTAction {
    override def withPath(path: Path): DELETE = copy(path = path)
    override def withParams(param: (String,Any)*): DELETE = copy( params = params++param )
    override def toString: String = s"DELETE '${path.mkString("/")}'"
  }

  def matchPrefix[T>:RESTAction](prefix: Path, action: RESTAction): Option[T] =
    Path.matchPrefix(prefix,action.path).map( action.withPath )


}


