
lazy val commonSettings = Seq(
  organization := "biz.enef",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.6",
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-Xlint")
)

lazy val root = project.in(file(".")).
  aggregate(surfJS, surfJVM).
  settings(
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
      "com.lihaoyi" %% "utest" % "0.3.1" % "test"
    )
  ).
  jvmSettings(
  ).
  jsSettings(
  )



lazy val surfJVM = surf.jvm

lazy val surfJS = surf.js

//lazy val akka = project.dependsOn( jvm )

//lazy val rest = project.dependsOn( akka )

