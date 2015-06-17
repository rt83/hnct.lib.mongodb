name := """fudivery.lib.mongodb"""

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
	"org.mongodb" %% "casbah" % "2.8.1",
  "com.google.code.gson" % "gson" % "2.2.4",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "com.typesafe.play" %% "play-json" % "2.3.4",
  "com.owlike" % "genson-scala_2.11" % "1.3",
  "io.spray" %%  "spray-json" % "1.3.2",
	"org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)