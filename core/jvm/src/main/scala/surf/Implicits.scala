//     Project: surf (https://github.com/jokade/surf)
//      Module: core / jvm
// Description: Global default implicits for JVM

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf

// !!! IMPORTANT: this object is defined in separate files for JVM and JS; KEEP both in sync !!!

/**
 * Provides global default implicits for surf/JVM.
 */
object Implicits {

  /**
   * The default [[CompletableFactory]] (JVM).
   */
  implicit final val globalCF: CompletableFactory = JVMCompletableFactory
}
