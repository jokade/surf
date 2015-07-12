//     Project: surf (https://github.com/jokade/surf)
//      Module: core / shared
// Description: A service wrapper that executes a function before and after the request is processed by the wrapped instance

// Copyright (c) 2015 Johannes Kastner <jokade@karchedon.de>
//               Distributed under the MIT license (see included LICENSE file)
package surf.service

import surf.{ServiceProps, Request, Service}

/**
 * A service wrapper that executes a function `before` and `after` the request is processed by the wrapped
 * service instance.
 *
 * @constructor
 * @param service wrapped service instance
 * @param before called with the request before `service` is executed
 * @param after called with the request after `service` has executed
 */
class ServiceWrapper(service: Service, before: (Any,Request)=>Unit, after: (Any,Request)=>Unit) extends Service {
  override def process = {
    case x =>
      before(x,request)
      service.handle(request,x)
      after(x,request)
  }
}

object ServiceWrapper {
  def apply(before: (Any,Request)=>Unit)(after: (Any,Request)=>Unit)(service: =>Service) : ServiceProps = ServiceProps(
    new ServiceWrapper(service,before,after)
  )
}


