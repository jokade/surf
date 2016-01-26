// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: ServiceProps is a configuration object used in createing a Service
//
// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

import surf.ServiceProps.ServiceType

/**
 * Configuration object used by [[ServiceRefFactory]]S to create [[ServiceRef]]S.
 */
trait ServiceProps {
  def serviceType: ServiceType
  def createService() : Service
}

object ServiceProps {
  type ServiceType = String

  case class ConstructorProps(create: ()=>Service, serviceType: ServiceType) extends ServiceProps {
    override def createService(): Service = create()
  }

  def apply(create: =>Service) : ServiceProps = ConstructorProps( ()=>create , "")
  def apply(serviceType: ServiceType, create: =>Service) : ServiceProps = ConstructorProps( ()=>create, serviceType)
}