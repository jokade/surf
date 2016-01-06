// -   Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Factory for REST RequestS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.rest

import surf.Request

import scala.concurrent.{Promise, ExecutionContext}

sealed trait RESTRequest extends Request {
  override def input: RESTAction
}

object RESTRequest {
  import RESTAction._

  def get(path: Path, params: Params = Map(), annotations: Map[String,Any] = Map())(implicit ec: ExecutionContext) : RESTRequest =
    new Impl(GET(path,params))

  def put(path: Path, body: Body = "", params: Params = Map(), annotations: Map[String,Any] = Map())(implicit ec: ExecutionContext) : RESTRequest =
    new Impl(PUT(path,params,body))

  private class Impl(action: RESTAction)(implicit ec: ExecutionContext)
    extends Request.Impl[RESTAction](action,Promise[Any](),null,null) with RESTRequest
}

