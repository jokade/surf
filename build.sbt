
lazy val commonSettings = Seq(
  organization := "biz.enef",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.6",
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-Xlint")
)

lazy val root = project.in(file(".")).
  aggregate(surfJS, surfJVM, akka).
  settings(commonSettings:_*).
  settings(
    name := "surf",
    publish := {},
    publishLocal := {}
  )

lazy val surf = crossProject.in(file(".")).
  enablePlugins(ScalaJSPlugin).
  settings(commonSettings:_*).
  settings(
    name := "surf",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "0.2.8",
      "com.lihaoyi" %%% "utest" % "0.3.1" % "test"
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  ).
  jvmSettings(
  ).
  jsSettings(
    preLinkJSEnv := NodeJSEnv().value,
    postLinkJSEnv := NodeJSEnv().value
  )



lazy val surfJVM = surf.jvm

lazy val surfJS = surf.js


lazy val akka = project.
  dependsOn( surfJVM ).
  settings(commonSettings:_*).
  settings(
    name := "surf-akka",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.3.+"
    )
  )


lazy val rest = project.
  dependsOn( akka ).
  settings(commonSettings:_*).
  settings(
    name := "surf-akka-rest",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-scala-experimental" % "1.0-RC2" exclude("com.typesafe.akka","akka-persistence-experimental_2.11")
    )
  )

lazy val nodejs = project.
  enablePlugins(ScalaJSPlugin).
  dependsOn( surfJS ).
  settings(commonSettings:_*).
  settings(
    name := "surf-nodejs",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "utest" % "0.3.1" % "test"
    ),
    testFrameworks += new TestFramework("utest.runner.Framework")
  )
