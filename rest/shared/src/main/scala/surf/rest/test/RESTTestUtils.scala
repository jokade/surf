//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared / test
// Description: Helper functions for testing RESTServices.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest.test

import surf.ServiceRef
import surf.dsl._
import surf.rest.RESTAction.GET
import surf.rest.RESTResponse.{OK, ResponseWriter, StringWriter}
import surf.rest.{ContentType, Path}

import scala.concurrent.{ExecutionContext, Future}

trait RESTTestUtils {

  def checkGetOk(path: String, responseType: String = ContentType.JSON)
                (checkResult: String => Any)(implicit eut: ServiceRef, ec: ExecutionContext): Future[Any] = {
    (GET(Path(path),Map()) >> eut).future.map{
      case OK(w,`responseType`) if w.isLeft => checkResult(mkString(w))
    }
  }

  private def mkString(w: ResponseWriter): String =
    if(w.isLeft) {
      val buf = StringWriter()
      w.left.get.apply(buf)
      buf.toString
    }
    else ???
}
