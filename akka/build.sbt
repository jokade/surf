name := "surf-akka"

// shared sources
//unmanagedSourceDirectories in Compile += baseDirectory.value / "shared" / "main" / "scala"

//unmanagedSourceDirectories in Test += baseDirectory.value / "shared" / "test" / "scala"

libraryDependencies ++= Seq(
  //"com.typesafe.akka" %% "akka-agent" % "2.3.+"
  "com.typesafe.akka" %% "akka-actor" % "2.3.+"
)

// directory for local publishing
publishTo := Some(Resolver.file("file", baseDirectory.value / ".." / "maven"  ))
