// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines the interface for ServiceRefRegistryS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import surf.ServiceRefRegistry.ServiceRefRegistryException

/**
 * Interface implemented by registries that provide access to [[ServiceRef]]S via a path.
 */
trait ServiceRefRegistry {

  /**
   * Returns a ServiceRef for the service at the specified path.
   *
   * @param path
   *
   * @throws ServiceRefRegistryException if the requested service does not exist
   */
  def serviceAt(path: String) : ServiceRef

  /**
   * Registers additonal services to this registry.
   *
   * @param services
   *
   * @throws ServiceRefRegistryException if one of the specified service paths is already in use
   */
  def registerServices(services: (String,ServiceProps)*) : Unit

  /**
   * Returns all paths for which a service is registered.
   */
  def registeredPaths: Iterable[String]

  /**
   * Broadcasts the specified message to all registered services
   *
   * @param msg
   */
  //def broadcast(msg: Any) : Unit
}


object ServiceRefRegistry {
  def singletonRegistry(factory: ServiceRefFactory, services: (String,ServiceProps)*) : ServiceRefRegistry =
    singletonRegistry(factory, services.toMap)

  /**
   * Creates a ServiceRefRegistry that caches a single instance for each registered Service.
   *
   * @param factory
   * @param services
   */
  def singletonRegistry(factory: ServiceRefFactory, services: Map[String,ServiceProps]) : ServiceRefRegistry =
    new CachingServiceRefRegistry(factory,services)

  /**
   * A ServiceRefRegistry that caches a single ServiceRef instance for each registered Service.
   *
   * @param factory Factory used to create new ServiceRef instances
   * @param initialServices initial set of registered services
   */
  class CachingServiceRefRegistry(factory: ServiceRefFactory, initialServices: Map[String,ServiceProps]) extends ServiceRefRegistry {
    private var _services = initialServices
    private var _cache = Map.empty[String,ServiceRef]

    override def serviceAt(path: String) = _cache.getOrElse(path, createServiceRef(path))

    override def registerServices(services: (String,ServiceProps)*) : Unit = this.synchronized{
      services.foreach{ s =>
        if(_services.contains(s._1))
          throw new ServiceRefRegistryException(s"Cannot register service '${s._1}': path already in use!")
      }
      _services ++= services
    }

    override def registeredPaths =  _services.keys

    //override def broadcast(msg: Any) : Unit = _cache.values.foreach( _.!(msg:Any) )

    private def createServiceRef(path: String) : ServiceRef = this.synchronized {
      if(_cache.contains(path)) _cache(path)
      else {
        val props = _services.getOrElse(path, throw new ServiceRefRegistryException(s"No service registered for path '$path'"))
        val ref = factory.serviceOf(props)
        _cache += path -> ref
        ref
      }
    }
  }

  class ServiceRefRegistryException(msg: String) extends RuntimeException(msg)
}
