//     Project: surf (https://github.com/jokade/surf)
//      Module: core/ test / shared
// Description: Tests for PromiseCompletable

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test.completable

import surf.Completable
import surf.Completable.PromiseCompletable

object PromiseCompletableTest extends CompletableBehaviour {
  implicit def executionContext = surf.Implicits.globalCF.executionContext
  override def createEUT(): Completable = new PromiseCompletable
}
