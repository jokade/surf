//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Interface for instances that resolve a RESTService for a given path.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest

import surf.ServiceRef

/**
 * Resolves RESTActions into [[RESTService]]s.
 */
trait RESTResolver {

  /**
   * Returns a ServiceRef to the RESTService to be used for the specified URL path,
   * or None if there is no RESTService registered to handle the specified action.
   * The second argument in the returned tuple represents the RESTAction to be used with
   * the resolved service (which may differ from the provided `action`)
   *
   * @param action RESTAction for which the handling service shall be resolved.
   * @return Tuple containing the resolved service ref and the updated RESTAction to be sent to this service.
   */
  def resolveRESTService(action: RESTAction) : Option[(ServiceRef,RESTAction)]

}

object RESTResolver {

  /**
   * Creates a RESTResolver that uses the specified list of mappings to handle path prefixes by sub-resolvers.
   *
   * @param prefixMappings Mappings from path prefixes to sub-resolvers
   */
  def fromPrefixes(prefixMappings: (Path,RESTResolver)*): RESTResolver = new PrefixResolver(prefixMappings.toMap)

  trait Wrapper extends RESTResolver {
    def subresolvers: Iterable[RESTResolver]
    // TODO: better algorithm?
    override def resolveRESTService(action: RESTAction) = subresolvers.view.map( _.resolveRESTService(action) ).collectFirst{
      case Some(m) => m
    }
  }

  trait Prefix extends RESTResolver {
    def prefixes: Iterable[(Path,RESTResolver)]
    override def resolveRESTService(action: RESTAction) = prefixes.view.
      map( p => (dropPathPrefix(p._1,action.path),p._2) ).
      find( _._1.isDefined ).
      flatMap( p => p._2.resolveRESTService(action.withPath(p._1.get)) )
  }

  class PrefixResolver(val prefixes: Map[Path,RESTResolver]) extends Prefix

}