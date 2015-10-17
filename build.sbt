name := """hnct.lib.mongodb"""

version := "1.0"

lazy val root = (project in file(".")).enablePlugins(DefaultConfigPlugin )

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
	"hnct" % "hnct.lib.utility" % "1.0.0-SNAPSHOT",
	"hnct" % "hnct.lib.config" % "1.0.0-SNAPSHOT",
	"com.github.nscala-time" %% "nscala-time" % "2.0.0",
	"org.mongodb" %% "casbah" % "2.8.1",
  "org.json4s" %% "json4s-jackson" % "3.2.11",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
