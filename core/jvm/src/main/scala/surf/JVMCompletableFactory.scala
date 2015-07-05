// -   Project: surf (https://github.com/jokade/surf)
//      Module: jvm
// Description: Default CompletableFactory for JVM
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included LICENSE file)
package surf

import surf.Completable.PromiseCompletable

object JVMCompletableFactory extends CompletableFactory {

  implicit val executionContext = scala.concurrent.ExecutionContext.global
  override def createCompletable(): Completable = new PromiseCompletable

}
