//     Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: Interface for Completer factories; implementations are specific to JVM / JS and thus defined separately

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf

import scala.concurrent.ExecutionContext

/**
 * Interface for all factories that providing [[Completable]]S.
 */
trait CompletableFactory {

  /**
   * Creates a new request (flow) Completable
   */
  def createCompletable() : Completable

  /**
   * Returns the ExecutionContext used by / together with this CompletableFactory
   */
  implicit def executionContext: ExecutionContext

}
