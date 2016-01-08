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

  def get(handle: RESTAction=>Unit) : RESTHandler = {
    case a:GET if a.path.isEmpty => handle(a)
  }

  def get(path: String)(handle: RESTAction=>Unit) : RESTHandler = get(Path(path))(handle)

  def get(path: Path)(handle: RESTAction=>Unit) : RESTHandler = {
    case a:GET if a.path == path => handle(a.withPath(Path.empty))
  }

  def getPrefix(prefix: String, ignoreLeadingSlash: Boolean = true)(handle: RESTAction=>Unit) : RESTHandler =
    getPrefix(Path(prefix,ignoreLeadingSlash))(handle)

  def getPrefix(prefix: Path)(handle: RESTAction=>Unit) : RESTHandler = {
    case a:GET if Path.isPrefix(prefix,a.path) => handle(RESTAction.matchPrefix(prefix,a).get)
  }


  def put(handle: RESTAction=>Unit) : RESTHandler = {
    case a:PUT if a.path.isEmpty => handle(a)
  }

  def put(path: String)(handle: RESTAction=>Unit) : RESTHandler = put(Path(path))(handle)

  def put(path: Path)(handle: RESTAction=>Unit) : RESTHandler = {
    case a:PUT if a.path == path => handle(a)
  }


  def post(handle: RESTAction=>Unit) : RESTHandler = {
    case a:POST if a.path.isEmpty => handle(a)
  }

  def post(path: String)(handle: RESTAction=>Unit) : RESTHandler = post(Path(path))(handle)

  def post(path: Path)(handle: RESTAction=>Unit) : RESTHandler = {
    case a:POST if a.path == path => handle(a)
  }


  def delete(handle: RESTAction=>Unit) : RESTHandler = {
    case a:DELETE if a.path.isEmpty => handle(a)
  }

  def delete(path: String)(handle: RESTAction=>Unit) : RESTHandler = delete(Path(path))(handle)

  def delete(path: Path)(handle: RESTAction=>Unit) : RESTHandler = {
    case a:DELETE if a.path == path => handle(a)
  }

}
