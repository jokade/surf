scalatex.SbtPlugin.projectSettings

organization := "de.surfice"

name := "surf-guide"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.4"

lazy val guide = scalatex.ScalatexReadme(
  projectId = "guide",
  wd = file(""),
  url = "https://github.com/lihaoyi/scalatex/tree/master",
  source = "SurfGuide"
)

