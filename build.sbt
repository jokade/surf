
lazy val commonSettings = Seq(
  organization := "biz.enef",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.6",
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-Xlint"),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "utest" % "0.3.1" % "test"
  ),
  testFrameworks += new TestFramework("utest.runner.Framework")
)

lazy val root = project.in(file(".")).
  //aggregate(surfJS, surfJVM, akka, servlet).
  aggregate(coreJVM, coreJS).
  settings(commonSettings:_*).
  settings(
    name := "surf",
    publish := {},
    publishLocal := {}
  )

lazy val core = crossProject.
  settings(commonSettings:_*).
  settings(publishingSettings:_*).
  settings(
    name := "surf-core",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "0.2.8"
    )
  ).
  jvmSettings(
  ).
  jsSettings(
    //preLinkJSEnv := NodeJSEnv().value,
    //postLinkJSEnv := NodeJSEnv().value
  )



lazy val coreJVM = core.jvm
lazy val coreJS = core.js

/*
lazy val akka = project.
  dependsOn( surfJVM ).
  settings(commonSettings:_*).
  settings(publishingSettings:_*).
  settings(
    name := "surf-akka",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.3.+"
    )
  )


lazy val rest = project.
  dependsOn( akka ).
  settings(commonSettings:_*).
  settings(publishingSettings:_*).
  settings(
    name := "surf-akka-rest",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-scala-experimental" % "1.0-RC2" exclude("com.typesafe.akka","akka-persistence-experimental_2.11")
    )
  )

lazy val servlet = project.
  dependsOn( surfJVM ).
  settings(commonSettings:_*).
  settings(publishingSettings:_*).
  settings(
    name := "surf-rest-servlet",
    libraryDependencies ++= Seq(
      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
    )
  )
  */  

lazy val publishingSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <url>https://github.com/jokade/surf</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>http://www.opensource.org/licenses/mit-license.php</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:jokade/surf</url>
      <connection>scm:git:git@github.com:jokade/surf.git</connection>
    </scm>
    <developers>
      <developer>
        <id>jokade</id>
        <name>Johannes Kastner</name>
        <email>jokade@karchedon.de</email>
      </developer>
    </developers>
  )
)
 
