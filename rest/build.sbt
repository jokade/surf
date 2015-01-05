name := "surf-akka-rest"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0-M1" exclude("com.typesafe.akka","akka-persistence-experimental_2.11")
)

// directory for local publishing
publishTo := Some(Resolver.file("file", baseDirectory.value / ".." / "maven"  ))
