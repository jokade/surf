// -   Project: surf (https://github.com/jokade/surf)
//      Module: nodejs
// Description:
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.js

import surf.{ServiceProps, ServiceRef, ServiceRefFactory}

import scala.scalajs.js

class WorkerServiceRefFactory extends ServiceRefFactory {

  override def serviceOf(props: ServiceProps): ServiceRef = new WorkerServiceRef(props.createService())
}

object WorkerServiceRefFactory {


}
