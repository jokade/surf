//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Common types + utility functions for the REST module.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf

package object rest {
  type Path = Seq[String]
  type Params = Map[String,Any]
  type Body = String

  /**
   * Checks if `path` starts with the specified `prefix` and then returns
   * the remaning path suffix. Returns None if the path does not begin with
   * the specified prefix.
   */
  @annotation.tailrec
  def dropPathPrefix(prefix: Path, path: Path) : Option[Path] =
    if(prefix.isEmpty) Some(path)
    else if(path.isEmpty || prefix.head != path.head ) None
    else dropPathPrefix(prefix.tail,path.tail)
}
