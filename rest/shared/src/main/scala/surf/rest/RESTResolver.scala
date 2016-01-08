//     Project: surf (https://github.com/jokade/surf)
//      Module: rest / shared
// Description: Interface for instances that resolve a RESTService for a given path.

// Copyright (c) 2016 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.rest

import surf.{ServiceRefFactory, ServiceProps, ServiceRef}

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
   * Creates a RESTResolver that delegates to a collection of resolvers until a resolver is found
   * that can handle the specified RESTAction.
   *
   * @param resolvers sub-resolvers to which all calls are delegated
   */
  def fromResolvers(resolvers: Iterable[RESTResolver]): RESTResolver = new WrapperResolver(resolvers)

  /**
   * Creates a RESTResolver that uses the specified list of mappings to handle path prefixes by sub-resolvers.
   *
   * @param prefixResolvers Mappings from path prefixes to sub-resolvers
   */
  def fromPrefixResolvers(prefixResolvers: (Path,RESTResolver)*): RESTResolver = new PrefixWrapperResolver(prefixResolvers.toMap)

  def fromPrefixMappings(prefixMappings: (Path,ServiceProps)*)(implicit f: ServiceRefFactory): RESTResolver =
    new MappingResolver(prefixMappings.toMap)

  trait Wrapper extends RESTResolver {
    def subresolvers: collection.IterableView[RESTResolver,Iterable[RESTResolver]]
    // TODO: better algorithm?
    override def resolveRESTService(action: RESTAction) = subresolvers.map( _.resolveRESTService(action) ).collectFirst{
      case Some(m) => m
    }
  }

  class WrapperResolver(resolvers: Iterable[RESTResolver]) extends Wrapper {
    override val subresolvers = resolvers.view
  }

  trait PrefixWrapper extends RESTResolver {
    def prefixes: Iterable[(Path,RESTResolver)]
    override def resolveRESTService(action: RESTAction) = prefixes.view.
      map( p => (Path.matchPrefix(p._1,action.path),p._2) ).
      find( _._1.isDefined ).
      flatMap( p => p._2.resolveRESTService(action.withPath(p._1.get)) )
  }

  class PrefixWrapperResolver(val prefixes: Map[Path,RESTResolver]) extends PrefixWrapper

  trait Mapping extends RESTResolver {
    def mappings: collection.IterableView[(Path,ServiceRef),Iterable[(Path,ServiceRef)]]
    override def resolveRESTService(action: RESTAction) = mappings.
      map( p => (RESTAction.matchPrefix(p._1,action),p._2) ).
      collectFirst{
        case (Some(act),service) => (service,act)
      }
  }

  class MappingResolver(map: Map[Path,ServiceRef]) extends Mapping {
    def this(map: Map[Path,ServiceProps])(implicit f: ServiceRefFactory) =
      this( map.map( p => p.copy(_2 = f.serviceOf(p._2))) )

    override val mappings = map.view
  }
}