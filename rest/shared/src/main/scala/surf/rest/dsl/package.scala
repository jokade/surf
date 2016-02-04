//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Directives and utility functions for the surf REST DSL

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest

import surf.Request
import surf.rest.RESTAction.{DELETE, PUT, POST, GET}
import surf.rest.RESTResponse._

/**
 * Directives for the surf REST DSL.
 */
package object dsl {

  implicit class RESTHandlerDSL(val handler: RESTHandler) extends AnyVal {
    def ~(next: RESTHandler) : RESTHandler = handler orElse next
  }

  def prefix(path: Path)(handle: RESTHandler)(implicit rp: RequestProvider) : RESTHandler = {
    case a if Path.isPrefix(path,a.path) => handle.applyOrElse(RESTAction.matchPrefix(path,a).get, notFound)
  }

  @inline
  def prefix(path: String, ignoreLeadingSlash: Boolean = true)
            (handle: RESTHandler)(implicit rp: RequestProvider) : RESTHandler =
    prefix( Path(path,ignoreLeadingSlash) )(handle)

  def get(handle: RESTAction.GET=>Unit) : RESTHandler = {
    case a:GET if a.path.isEmpty => handle(a)
  }

  @inline
  def get(path: String)(handle: RESTAction.GET=>Unit) : RESTHandler = get(Path(path))(handle)

  def get(path: Path)(handle: RESTAction.GET=>Unit) : RESTHandler = {
    case a:GET if a.path == path => handle(a.withPath(Path.empty))
  }


  def put(handle: RESTAction.PUT=>Unit) : RESTHandler = {
    case a:PUT if a.path.isEmpty => handle(a)
  }

  @inline
  def put(path: String)(handle: RESTAction.PUT=>Unit) : RESTHandler = put(Path(path))(handle)

  def put(path: Path)(handle: RESTAction.PUT=>Unit) : RESTHandler = {
    case a:PUT if a.path == path => handle(a)
  }


  def post(handle: RESTAction.POST=>Unit) : RESTHandler = {
    case a:POST if a.path.isEmpty => handle(a)
  }

  @inline
  def post(path: String)(handle: RESTAction.POST=>Unit) : RESTHandler = post(Path(path))(handle)

  def post(path: Path)(handle: RESTAction.POST=>Unit) : RESTHandler = {
    case a:POST if a.path == path => handle(a)
  }


  def delete(handle: RESTAction.DELETE=>Unit) : RESTHandler = {
    case a:DELETE if a.path.isEmpty => handle(a)
  }

  @inline
  def delete(path: String)(handle: RESTAction.DELETE=>Unit) : RESTHandler = delete(Path(path))(handle)

  def delete(path: Path)(handle: RESTAction.DELETE=>Unit) : RESTHandler = {
    case a:DELETE if a.path == path => handle(a)
  }

//  @inline
//  def bool(name: String)(implicit act: RESTAction): Option[Boolean] = BooleanParam(name)
//
//  @inline
//  def bool(name: String, default: =>Boolean)(implicit act: RESTAction): Boolean = BooleanParam(name,default)

  // TODO: better way to extract the param?
  def bool(name: String)(handle: RESTHandler)(implicit rp: RequestProvider) : RESTHandler = {
    case GET(Seq(BooleanParam(p), xs @ _*),params) => handle.applyOrElse(GET(xs,params.updated(name,p)),notFound)
    case PUT(Seq(BooleanParam(p), xs @ _*),params,body) => handle.applyOrElse(PUT(xs,params.updated(name,p),body),notFound)
    case POST(Seq(BooleanParam(p), xs @ _*),params,body) => handle.applyOrElse(POST(xs,params.updated(name,p),body),notFound)
    case DELETE(Seq(BooleanParam(p), xs @ _*),params) => handle.applyOrElse(DELETE(xs,params.updated(name,p)),notFound)
  }


//  @inline
//  def int(name: String)(implicit act: RESTAction): Option[Int] = IntParam(name)
//
//  @inline
//  def int(name: String, default: =>Int)(implicit act: RESTAction): Int = IntParam(name,default)

  // TODO: better way to extract the param?
  def int(name: String)(handle: RESTHandler)(implicit rp: RequestProvider) : RESTHandler = {
    case GET(Seq(IntParam(p), xs @ _*),params) => handle.applyOrElse(GET(xs,params.updated(name,p)),notFound)
    case PUT(Seq(IntParam(p), xs @ _*),params,body) => handle.applyOrElse(PUT(xs,params.updated(name,p),body),notFound)
    case POST(Seq(IntParam(p), xs @ _*),params,body) => handle.applyOrElse(POST(xs,params.updated(name,p),body),notFound)
    case DELETE(Seq(IntParam(p), xs @ _*),params) => handle.applyOrElse(DELETE(xs,params.updated(name,p)),notFound)
  }

//  @inline
//  def string(name: String)(implicit act: RESTAction): Option[String] = StringParam(name)
//
//  @inline
//  def string(name: String, default: =>String)(implicit act: RESTAction): String = StringParam(name,default)

  @inline
  def ok(body: String, ctype: ContentType = ContentType.PLAIN)(implicit rp: RequestProvider) : Unit = rp.request ! OK(body,ctype)

  @inline
  def respondWithResource(path: String, ctype: ContentType, status: Int = 200)(implicit rp: RequestProvider) : Unit =
    rp.request ! RespondWithResource(path,ctype,status)

  @inline
  def noContent(implicit rp: RequestProvider) : Unit = rp.request ! NoContent

  @inline
  def notFound(implicit rp: RequestProvider) : Unit = rp.request ! NotFound

  @inline
  def conflict(msg: String)(implicit rp: RequestProvider) : Unit = rp.request ! Conflict(msg)

  @inline
  def badRequest(msg: String)(implicit rp: RequestProvider) : Unit = rp.request ! BadRequest(msg)

  @inline
  def notFound(act: RESTAction)(implicit rp: RequestProvider) : Unit = notFound

  /**
   * Serves GET requests to static resources.
   *
   * @example ```
   * prefix("resource") {
   *   serveStatic {
   *     case PathWithSuffix(path,"js") => ("js/"+path,"text/javascript")
   *     case PathWithSuffix(path,_)    => (path,"text/plain")
   *   }
   * }
   * ```
   *
   * @param handle A partial function that returns a tuple containing the resolved path of the resource to be served
   *               and the [[ContentType]] of the resource.
   * @param rp
   */
  def serveStatic(handle: PartialFunction[Path,(String,ContentType)])(implicit rp: RequestProvider) : RESTHandler = {
    case GET(path,_) => handle.applyOrElse(path, (_:Path) => (null:String,null:ContentType) ) match {
      case (null,null) => notFound
      case (resource,ctype) => respondWithResource(resource,ctype)
    }
  }

  /**
   * Serves GET requests to a static resource.
   *
   * @example ```
   * prefix("resource") {
   *   serveStatic("/path/to/resources")
   * }
   * ```
   *
   * @param prefix Prefix path used to resolve all resource requests (i.e. the base directory)
   * @param rp
   */
  def serveStatic(prefix: String)(implicit rp: RequestProvider) : RESTHandler = {
    case GET(PathWithContentType(file,ctype),_) => respondWithResource(prefix + file, ctype)
  }

  object PathWithSuffix {
    /**
     * Splits a [[Path]] into the full path string and the file suffix (ie the string after the last '.')
     *
     * @param p
     */
    def unapply(p: Path) : Option[(String,String)] =
      if(p.isEmpty) None
      else p.last.split("\\.") match {
        case s if s.size == 1 =>
          Some((p.mkString("/"),""))
        case s =>
          Some((p.mkString("/"),s.last))
        }
  }

  object PathWithContentType {
    /**
     * Splits a [[Path]] into the full path string and the [[ContentType]] as indicated by the file suffix.
     *
     * @param p
     */
    def unapply(p: Path): Option[(String, ContentType)] = PathWithSuffix.unapply(p) match {
      case Some((file,suffix)) => ContentType.fromSuffix(suffix) map ((file,_))
      case _ => None
    }
  }
}

package dsl {
  trait RequestProvider {
    def request: Request
  }
}
