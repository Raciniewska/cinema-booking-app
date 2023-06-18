name := "cinema-api-rest"

version := "1.0"

scalaVersion := "2.10.2"

ThisBuild / javacOptions ++= Seq("-source", "8", "-target", "8")

libraryDependencies ++= Seq(
  "io.spray" % "spray-can" % "1.1-M8",
  "io.spray" % "spray-http" % "1.1-M8",
  "io.spray" % "spray-routing" % "1.1-M8",
  "com.typesafe.akka" %% "akka-actor" % "2.1.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.1.4",
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "net.liftweb" %% "lift-json" % "2.5.1",
  "ch.qos.logback" % "logback-classic" % "1.0.13" % Runtime,
  "org.postgresql" % "postgresql" % "42.5.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4" % "provided"
)

resolvers += ("Spray repository" at "http://repo.spray.io").withAllowInsecureProtocol(true)
resolvers += ("Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/").withAllowInsecureProtocol(true)

