// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: Defines the interface for ServiceRefRegistryS
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

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
   * Broadcasts the specified message to all registered services
   *
   * @param msg
   */
  def broadcast(msg: Any) : Unit
}

class ServiceRefRegistryException(msg: String) extends RuntimeException(msg)

object ServiceRefRegistry {
  def singletonRegistry(factory: ServiceRefFactory, services: (String,ServiceProps)*) : ServiceRefRegistry =
    singletonRegistry(factory, services.toMap)

  def singletonRegistry(factory: ServiceRefFactory, services: Map[String,ServiceProps]) : ServiceRefRegistry =
    new CachingServiceRefRegistry(factory,services)

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

    override def broadcast(msg: Any) : Unit = _cache.values.foreach( _.!(msg:Any) )

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
}
