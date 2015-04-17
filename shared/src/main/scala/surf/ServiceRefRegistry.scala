// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines the interface for ServiceRefRegistryS
//
// Copyright (c) 2015 Johannes Kastner <jkspam@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

/**
 * Interface implemented by registries that provide access to [[ServiceRef]]S via a path.
 */
trait ServiceRefRegistry {

  def serviceAt(path: String) : ServiceRef

}

class ServiceRefRegistryException(msg: String) extends RuntimeException(msg)

object ServiceRefRegistry {
  def apply(factory: ServiceRefFactory, services: (String,ServiceProps)*) : ServiceRefRegistry =
    apply(factory, services.toMap)

  def apply(factory: ServiceRefFactory, services: Map[String,ServiceProps]) : ServiceRefRegistry =
    new CachingServiceRefRegistry(factory,services)

  class CachingServiceRefRegistry(factory: ServiceRefFactory, services: Map[String,ServiceProps]) extends ServiceRefRegistry {
    private var _cache = Map.empty[String,ServiceRef]

    override def serviceAt(path: String) = _cache.getOrElse(path, createServiceRef(path))

    private def createServiceRef(path: String) : ServiceRef = this.synchronized {
      if(_cache.contains(path)) _cache(path)
      else {
        val props = services.getOrElse(path, throw new ServiceRefRegistryException(s"No service registered for path '$path'"))
        val ref = factory.serviceOf(props)
        _cache += path -> ref
        ref
      }
    }
  }
}
