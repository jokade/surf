//     Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: Defines common types and utility functions.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package object surf {

  /// Request annotations
  type Annotations = Map[String,Any]

  /// Type for message processing functions
  type Processor = PartialFunction[Any,Unit]
}
