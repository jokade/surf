//     Project: surf (https://github.com/jokade/surf)
//      Module:
// Description:

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.test

import utest._

import scala.concurrent.{ExecutionContext, Future}

trait TestBase extends TestSuite {

  case object Ex extends RuntimeException

  case object ExpectedFailure extends RuntimeException

  def expectFailure(f: Future[_])(implicit ec: ExecutionContext): Future[Any] =
    f.map( _ => throw ExpectedFailure).recoverWith{
      case ExpectedFailure => Future.failed(ExpectedFailure)
      case ex:Throwable => Future.successful(ex)
    }

  def merge(fs: Future[Any]*)(implicit ec: ExecutionContext): Future[Any] = Future.reduce(fs)((_,_)=>true)
}
