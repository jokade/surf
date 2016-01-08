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
    'Path- {
      'apply-{
        import surf.rest.Path
        assert( Path("") == Path.empty )
        assert( Path("  ") == Path.empty )
        assert( Path(" / ") == Path.empty )
        assert( Path("foo") == Seq("foo") )
        assert( Path("/foo") == Seq("foo") )
        assert( Path(" foo/bar ") == Seq("foo","bar") )
        assert( Path("/ foo/bar ") == Seq(" foo","bar") )

        assert( Path("",false) == Path.empty )
        assert( Path("  ",false) == Path.empty )
        assert( Path(" / ",false) == Seq("") )
        assert( Path("foo",false) == Seq("foo") )
        assert( Path("/foo",false) == Seq("","foo") )
        assert( Path(" foo/bar ",false) == Seq("foo","bar") )
        assert( Path("/ foo/bar ",false) == Seq(""," foo","bar") )
      }
      'isPrefix-{
        import surf.rest.Path
        import surf.rest.Path.isPrefix
        assert( isPrefix(Path("",true),Path("/foo")) )
        assert( isPrefix(Path("/",true),Path("/foo")) )
        assert( isPrefix(Path("  ",true),Path("/foo")) )
        assert( isPrefix(Path("/",true),Path("foo")) )
        assert( !isPrefix(Path("/bar",true),Path("foo")) )
        assert( isPrefix(Path("/foo/bar",true),Seq("foo","bar")) )
        assert( isPrefix(Path("/foo/bar/",true),Seq("foo","bar")) )

        assert( isPrefix(Path("",false),Path("/foo",false)) )
        assert( isPrefix(Path("/",false),Path("/foo",false)) )
        assert( isPrefix(Path("  ",false),Path("/foo",false)) )
        assert( !isPrefix(Path("/",false),Path("foo",false)) )
        assert( !isPrefix(Path("/bar",false),Path("foo",false)) )
        assert( isPrefix(Path("/foo/bar",false),Path("/foo/bar",false)) )
        assert( isPrefix(Path("/foo/bar/",false),Path("/foo/bar",false)) )
      }
      'matchPrefix - {
        import surf.rest.Path.matchPrefix
        assert(matchPrefix(Seq("foo"), Seq("foo", "bar")) == Some(Seq("bar")))
        assert(matchPrefix(Seq("foo", "baz"), Seq("foo", "bar")) == None)
        assert(matchPrefix(Seq("foo", "bar", "x"), Seq("foo", "bar")) == None)
        assert(matchPrefix(Seq(), Seq("foo", "bar")) == Some(Seq("foo", "bar")))
        assert(matchPrefix(Seq(), Seq()) == Some(Seq()))
        assert(matchPrefix(Seq("foo"), Seq()) == None)
      }
    }
  }
}
