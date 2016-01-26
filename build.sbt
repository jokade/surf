
lazy val commonSettings = Seq(
  organization := "de.surfice",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.6",
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-Xlint"),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "utest" % "0.3.1" % "test"
  ),
  testFrameworks += new TestFramework("utest.runner.Framework"),
  resolvers += Resolver.sonatypeRepo("snapshots")
)

lazy val root = project.in(file(".")).
  aggregate(coreJVM, coreJS, restJVM, restJS, akka, rest_servlet, rest_nodejs).
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
    name := "surf-core"
  ).
  jvmSettings(
  ).
  jsSettings(
    //preLinkJSEnv := NodeJSEnv().value,
    //postLinkJSEnv := NodeJSEnv().value
  )

lazy val coreJVM = core.jvm
lazy val coreJS = core.js


lazy val rest = crossProject.
  dependsOn(core % "compile->compile;test->test").
  settings(commonSettings:_*).
  settings(publishingSettings:_*).
  settings(
    name := "surf-rest",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % "0.2.8",
      "biz.enef"    %% "slogging" % "0.4.0"
    )
  )

lazy val restJVM = rest.jvm
lazy val restJS = rest.js


lazy val akka = project.
  dependsOn( coreJVM % "compile->compile;test->test" ).
  settings(commonSettings:_*).
  settings(publishingSettings:_*).
  settings(
    name := "surf-akka",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.3.+"
    )
  )


lazy val rest_servlet = project.
  dependsOn( restJVM ).
  settings(commonSettings:_*).
  settings(publishingSettings:_*).
  settings(
    name := "surf-rest-servlet",
    libraryDependencies ++= Seq(
      "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided"
    )
  )

lazy val rest_akka = project.
  dependsOn( restJVM, akka ).
  settings(commonSettings:_*).
  settings(publishingSettings:_*).
  settings(
    name := "surf-rest-akka",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http-scala-experimental" % "1.0-RC2" exclude("com.typesafe.akka","akka-persistence-experimental_2.11")
    )
  )

lazy val rest_nodejs = project.
  enablePlugins(ScalaJSPlugin).
  dependsOn( restJS ).
  settings(commonSettings:_*).
  settings(publishingSettings:_*).
  settings(
    name := "surf-rest-nodejs",
    libraryDependencies ++= Seq(
      "de.surfice" %%% "scalajs-nodejs" % "0.1-SNAPSHOT"
    )
  )

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
 
