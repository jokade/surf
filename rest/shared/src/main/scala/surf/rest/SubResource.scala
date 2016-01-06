// -   Project: surf (https://github.com/jokade/surf)
//      Module:
// Description:
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)

//     Project: root
//      Module:
// Description:
package surf.rest

import surf.{ServiceRef, ServiceRefFactory}
import RESTResource._

/*
case class SubResource(path: Path)(parent: RESTResource) extends RESTResource {
  override def name = path.last

  override def handler(implicit factory: ServiceRefFactory): ServiceRef = parent.handler

  override def child(path: Path): Option[RESTResource] = None
}
*/