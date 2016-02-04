surf
====
[![Build Status](https://travis-ci.org/jokade/surf.svg?branch=master)](https://travis-ci.org/jokade/surf) [![npm](https://img.shields.io/npm/l/express.svg)]()

A simple library for actor-style message passing and request-flow processing for Scala/JVM and Scala.js.
On the JVM, surf can be used as a thin abstraction layer on top of [Akka](http://akka.io).

### SBT settings
Add the following lines to your `build.sbt`:
```scala
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies += "de.surfice" %%% "surf-core" % "0.1-SNAPSHOT"
```
If you want to use the RESTful service DSL, you need to add
```scala
libraryDependencies += "de.surfice" %%% "surf-rest" % "0.1-SNAPSHOT"
```

Introduction
------------
*This section assumes some basic knowledge of message passing and [Akka-style actors](http://doc.akka.io/docs/akka/current/general/actors.html).*

Whereas Akka is an elaborate toolkit to build "*Build powerful concurrent & distributed applications*" (http://akka.io), SURF is a thin abstraction layer to simplify actor-style programming with pluggable backends, so that it may be used on the JVM and with [Scala.js](http://scala-js.org). Hence, SURF has no explicit notion of mailboxes, dispatchers, etc., but relies on the used backend to provide the necessary mechanisms.

In SURF, there only four basic types of entities:

* **Service**: encapsulates some application logic and communicates with its enviornment only via messages (a.k.a `Actor` in Akka)
* **ServiceRef**: an interface for communication with a service (a.k.a `ActorRef` in Akka). you never interact directly with a service instance, but instead you retrieve adirectly with a Service, but instead
* **ServiceRefFactory**: a factory to create ServiceRefs. You never interact directly with a Service instance; instead, you use a ServiceRefFactory (or ServiceRefRegsitry) to get a ServiceRef for a specific Service implementation.
* **Message**: any object sent to or received from a Service.


### Services
TBD


License
-------
This code is open source software licensed under the [MIT License](http://opensource.org/licenses/MIT)
