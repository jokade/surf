name := "surf-jvm"

// shared sources
unmanagedSourceDirectories in Compile += baseDirectory.value / "shared" / "main" / "scala"

unmanagedSourceDirectories in Test += baseDirectory.value / "shared" / "test" / "scala"

// directory for local publishing
publishTo := Some(Resolver.file("file", baseDirectory.value / ".." / "maven"  ))
