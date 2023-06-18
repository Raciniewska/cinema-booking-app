ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

libraryDependencies += "com.softwaremill.sttp.client3" %% "core" % "3.1.9"
libraryDependencies += "io.circe" %% "circe-core"%"0.14.5"
libraryDependencies += "io.circe" %% "circe-generic"%"0.14.5"
libraryDependencies += "io.circe" %% "circe-parser"%"0.14.5"
libraryDependencies += "com.lihaoyi" %% "ujson" % "3.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "cinema-client"
  )

