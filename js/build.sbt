import ScalaJSKeys._

name := "surf-js"

scalaJSSettings

// shared sources
unmanagedSourceDirectories in Compile += baseDirectory.value / "shared" / "main" / "scala"

unmanagedSourceDirectories in Test += baseDirectory.value / "shared" / "test" / "scala"

