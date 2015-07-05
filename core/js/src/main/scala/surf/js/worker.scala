// -   Project: surf (https://github.com/jokade/surf)
//      Module:
// Description:
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf.js

import scala.scalajs.js
import scala.scalajs.js.UndefOr

trait Worker extends js.Object {
  var onmessage : (Any) => Unit = js.native
  def postMessage(msg: Any) : Unit = js.native
  def terminate() : Unit = js.native
}

object WorkerFactory {
  lazy val isNodejs = js.Dynamic.global.global.asInstanceOf[UndefOr[js.Object]].isDefined

  def createWorker(f: js.ThisFunction) : Worker =
    if(isNodejs) {
      js.Dynamic.newInstance(js.Dynamic.global.require("webworker-threads").Worker)(f).asInstanceOf[Worker]
    }
    else ???
}

