//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared / test
// Description: Tests for utility function defined in the surf.rest package object

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest.test

import surf.test.TestBase
import utest._

object PackageTest extends TestBase {
  override val tests = TestSuite {
    'dropPathPrefix-{
      import surf.rest.dropPathPrefix
      assert( dropPathPrefix(Seq("foo"),Seq("foo","bar")) == Some(Seq("bar")) )
      assert( dropPathPrefix(Seq("foo","baz"),Seq("foo","bar")) == None )
      assert( dropPathPrefix(Seq("foo","bar","x"),Seq("foo","bar")) == None )
      assert( dropPathPrefix(Seq(),Seq("foo","bar")) == Some(Seq("foo","bar")) )
      assert( dropPathPrefix(Seq(),Seq()) == Some(Seq()) )
      assert( dropPathPrefix(Seq("foo"),Seq()) == None )
    }
  }
}
