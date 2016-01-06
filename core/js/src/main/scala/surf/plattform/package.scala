//     Project: surf (https://github.com/jokade/surf)
//      Module: core / js
// Description: JS specific utility methods required by the shared core implementation.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf

import scala.scalajs.js
import scala.scalajs.js.UndefOr

package object plattform {
  import scalajs.js.Dynamic.global

  private lazy val _invokeLater = global.setImmediate.asInstanceOf[UndefOr[js.Function]].getOrElse{
    (callback: js.Function) => global.setTimeout(callback,0)
  }.asInstanceOf[js.Function1[js.Function,Any]]

  @inline
  def invokeLater(callback: ()=>Any) : Unit = _invokeLater(callback)

}
