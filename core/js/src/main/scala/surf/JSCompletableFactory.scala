// -   Project: surf (https://github.com/jokade/surf)
//      Module: js
// Description: CompleterFactoryS are used to create CompletableS (JS implementation)
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import surf.Completable.PromiseCompletable

object JSCompletableFactory extends CompletableFactory {

  implicit val executionContext = scalajs.concurrent.JSExecutionContext.runNow
  override def createCompletable(): Completable = new PromiseCompletable

}
