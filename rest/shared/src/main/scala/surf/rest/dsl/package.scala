//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Directives and utility functions for the surf REST DSL

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest

import surf.rest.RESTAction.{DELETE, PUT, POST, GET}

/**
 * Directives for the surf REST DSL.
 */
package object dsl {

  implicit class RESTHandlerDSL(val handler: RESTHandler) extends AnyVal {
    def ~(next: RESTHandler) : RESTHandler = handler orElse next
  }

  def prefix(path: Path)(handle: RESTHandler)(implicit nfh: NotFoundHandler) : RESTHandler = {
    case a if Path.isPrefix(path,a.path) => handle.applyOrElse(RESTAction.matchPrefix(path,a).get, nfh.notFound)
  }

  @inline
  def prefix(path: String, ignoreLeadingSlash: Boolean = true)
            (handle: RESTHandler)(implicit notFound: NotFoundHandler) : RESTHandler =
    prefix( Path(path,ignoreLeadingSlash) )(handle)

  def get(handle: RESTAction=>Unit) : RESTHandler = {
    case a:GET if a.path.isEmpty => handle(a)
  }

  @inline
  def get(path: String)(handle: RESTAction=>Unit) : RESTHandler = get(Path(path))(handle)

  def get(path: Path)(handle: RESTAction=>Unit) : RESTHandler = {
    case a:GET if a.path == path => handle(a.withPath(Path.empty))
  }


  def put(handle: RESTAction=>Unit) : RESTHandler = {
    case a:PUT if a.path.isEmpty => handle(a)
  }

  @inline
  def put(path: String)(handle: RESTAction=>Unit) : RESTHandler = put(Path(path))(handle)

  def put(path: Path)(handle: RESTAction=>Unit) : RESTHandler = {
    case a:PUT if a.path == path => handle(a)
  }


  def post(handle: RESTAction=>Unit) : RESTHandler = {
    case a:POST if a.path.isEmpty => handle(a)
  }

  @inline
  def post(path: String)(handle: RESTAction=>Unit) : RESTHandler = post(Path(path))(handle)

  def post(path: Path)(handle: RESTAction=>Unit) : RESTHandler = {
    case a:POST if a.path == path => handle(a)
  }


  def delete(handle: RESTAction=>Unit) : RESTHandler = {
    case a:DELETE if a.path.isEmpty => handle(a)
  }

  @inline
  def delete(path: String)(handle: RESTAction=>Unit) : RESTHandler = delete(Path(path))(handle)

  def delete(path: Path)(handle: RESTAction=>Unit) : RESTHandler = {
    case a:DELETE if a.path == path => handle(a)
  }

//  @inline
//  def bool(name: String)(implicit act: RESTAction): Option[Boolean] = BooleanParam(name)
//
//  @inline
//  def bool(name: String, default: =>Boolean)(implicit act: RESTAction): Boolean = BooleanParam(name,default)

  // TODO: better way to extract the param?
  def bool(name: String)(handle: RESTHandler)(implicit nfh: NotFoundHandler) : RESTHandler = {
    case GET(Seq(BooleanParam(p), xs @ _*),params) => handle.applyOrElse(GET(xs,params.updated(name,p)),nfh.notFound)
    case PUT(Seq(BooleanParam(p), xs @ _*),params,body) => handle.applyOrElse(PUT(xs,params.updated(name,p),body),nfh.notFound)
    case POST(Seq(BooleanParam(p), xs @ _*),params,body) => handle.applyOrElse(POST(xs,params.updated(name,p),body),nfh.notFound)
    case DELETE(Seq(BooleanParam(p), xs @ _*),params) => handle.applyOrElse(DELETE(xs,params.updated(name,p)),nfh.notFound)
  }


//  @inline
//  def int(name: String)(implicit act: RESTAction): Option[Int] = IntParam(name)
//
//  @inline
//  def int(name: String, default: =>Int)(implicit act: RESTAction): Int = IntParam(name,default)

  // TODO: better way to extract the param?
  def int(name: String)(handle: RESTHandler)(implicit nfh: NotFoundHandler) : RESTHandler = {
    case GET(Seq(IntParam(p), xs @ _*),params) => handle.applyOrElse(GET(xs,params.updated(name,p)),nfh.notFound)
    case PUT(Seq(IntParam(p), xs @ _*),params,body) => handle.applyOrElse(PUT(xs,params.updated(name,p),body),nfh.notFound)
    case POST(Seq(IntParam(p), xs @ _*),params,body) => handle.applyOrElse(POST(xs,params.updated(name,p),body),nfh.notFound)
    case DELETE(Seq(IntParam(p), xs @ _*),params) => handle.applyOrElse(DELETE(xs,params.updated(name,p)),nfh.notFound)
  }

//  @inline
//  def string(name: String)(implicit act: RESTAction): Option[String] = StringParam(name)
//
//  @inline
//  def string(name: String, default: =>String)(implicit act: RESTAction): String = StringParam(name,default)
}
