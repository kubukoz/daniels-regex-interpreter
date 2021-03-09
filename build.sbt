ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.4"

lazy val root = (project in file(".")).settings(
  name := "ce3.g8",
  libraryDependencies ++= Seq(
    "co.fs2" %% "fs2-core" % "3.0.0-M9"
  )
)
