//     Project: surf (https://github.com/jokade/surf)
//      Module: core / jvm
// Description: JVM specific utility methods required by the shared core implementation.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf

import java.util.concurrent.Callable

package object plattform {

  @inline
  def invokeLater(runnable: Runnable) : Unit = concurrent.ExecutionContext.global.execute(runnable)

}
