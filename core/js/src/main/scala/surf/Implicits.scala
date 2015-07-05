//     Project: surf (https://github.com/jokade/surf)
//      Module: core / js
// Description: Global default implicits for JS

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf

// !!! IMPORTANT: this object is defined in separate files for JVM and JS; KEEP both in sync !!!

object Implicits {

  /**
   * The default [[CompletableFactory]] (JS).
   */
  implicit final val globalCF: CompletableFactory = JSCompletableFactory
}
