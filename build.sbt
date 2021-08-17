name := "libgcode"

organization := "com.github.dzufferey"

version := "0.1-SNAPSHOT"

scalaVersion := "2.13.6"

scalacOptions in Compile ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature"
)

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-math3" % "3.6.1",
  "com.lihaoyi" %% "fastparse" % "2.3.0",
  "org.scalatest" %% "scalatest" % "3.2.5" % "test",
  "com.github.dzufferey" %% "misc-scala-utils" % "1.0.0",
  "com.github.dzufferey" %% "almond-x3dom-model-viewer" % "0.2.3"
)

fork := true
