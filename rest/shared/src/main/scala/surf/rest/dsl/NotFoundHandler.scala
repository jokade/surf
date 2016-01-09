//     Project: Default (Template) Project
//      Module: rest / shared
// Description: Defines a handler that is executed by REST DSL functions if no match could be found

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest.dsl

import surf.rest.RESTAction

trait NotFoundHandler {
  def notFound(act: RESTAction): Unit
}
