name := """fudivery.lib.mongodb"""

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies += "org.mongodb" %% "casbah" % "2.8.1"

// Change this to another test framework if you prefer
libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"

