// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Factory for REST RequestS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import surf.rest.RESTResponse.ContentType
import surf.{Annotations, Request}

import scala.concurrent.{ExecutionContext, Promise}

sealed trait RESTRequest extends Request {
  override def input: RESTAction
}

object RESTRequest {
  import RESTAction._

  def apply(action: RESTAction, annotations: Annotations = Map())(implicit ec: ExecutionContext): RESTRequest =
    new Impl(action,annotations)

  def get(path: Path, params: Params = Map(), annotations: Map[String,Any] = Map())(implicit ec: ExecutionContext) : RESTRequest =
    new Impl(GET(path,params),annotations)

  def post(path: Path, body: Body, ctype: ContentType = ContentType.PLAIN, encoding: Encoding = "UTF-8", params: Params = Map(), annotations: Map[String,Any] = Map())(implicit ec: ExecutionContext) : RESTRequest =
    new Impl(POST(path,params,body,ctype,encoding),annotations)

  def put(path: Path, body: Body, ctype: ContentType = ContentType.PLAIN, encoding: Encoding = "UTF-8", params: Params = Map(), annotations: Map[String,Any] = Map())(implicit ec: ExecutionContext) : RESTRequest =
    new Impl(PUT(path,params,body,ctype,encoding),annotations)

  private final class Impl(action: RESTAction, annotations: Annotations)(implicit ec: ExecutionContext)
    extends Request.Impl[RESTAction](action,Promise[Any](),annotations,null) with RESTRequest
}

