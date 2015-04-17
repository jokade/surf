// -   Project: surf (https://github.com/jokade/surf)
//      Module: shared
// Description: ServiceProps is a configuration object used in createing a Service
//
// Copyright (c) 2015 Johannes Kastner <jkspam@karchedon.de>
//               Distributed under the MIT License (see included file LICENSE)
package surf

/**
 * Configuration object used in creating a [[Service]]
 */
trait ServiceProps {
  def createService() : Service
}

object ServiceProps {
  case class ConstructorProps(create: ()=>Service) extends ServiceProps {
    override def createService(): Service = create()
  }

  def apply(create: =>Service) : ServiceProps = ConstructorProps( ()=>create )

}