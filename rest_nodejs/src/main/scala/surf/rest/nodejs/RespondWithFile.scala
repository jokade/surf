//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / nodejs
// Description: Creates a successful response (OK/200) using the contents of a file.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest.nodejs

import nodejs.FS
import surf.rest.RESTResponse.OK
import surf.rest.{RESTContentType, RESTResponse}

object RespondWithFile {
  /**
   * Creates a successful HTTP response (OK/200) using the contents of the specified file.
   *
   * @param file Path to the file to be returned
   * @param contentType content type of the file
   */
  def apply(file: String, contentType: String = RESTContentType.PLAIN) : RESTResponse.OK =
    OK(FS().readFileSync(file),contentType)

}
