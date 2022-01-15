
name := """action-chaining-scala-play-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

libraryDependencies ++= Seq(
    jdbc,
    ws,
    guice
)