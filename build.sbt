name := "surf"

organization in ThisBuild := "de.karchedon"

version in ThisBuild := "0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.4"

scalacOptions in ThisBuild ++= Seq("-deprecation","-feature","-Xlint")

libraryDependencies in ThisBuild ++= Seq(
  "com.lihaoyi" %% "upickle" % "0.2.5",
  "com.lihaoyi" %% "utest" % "0.2.4" % "test"
)

lazy val jvm = project

lazy val js = project

lazy val akka = project.dependsOn( jvm )

lazy val rest = project.dependsOn( akka )

lazy val root = project.in( file(".") )
                .aggregate(jvm,js,akka,rest)
                .settings( publishTo := Some(Resolver.file("file",baseDirectory.value / "maven")) )
                .settings( publish := {} )
