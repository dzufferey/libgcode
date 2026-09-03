ThisBuild / scalaVersion := "3.3.8"

lazy val root = (project in file("."))
  .settings(
    name := "libgcode",
    organization := "com.github.dzufferey",
    version := "0.1-SNAPSHOT",
    Compile / scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature"
    ),
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-math3" % "3.6.1",
      "com.lihaoyi"       %% "fastparse"     % "3.1.1",
      "org.scalatest"     %% "scalatest"     % "3.2.20" % "test"
    )
  )

lazy val examples = (project in file("examples"))
  .dependsOn(root)
  .settings(
    name := "libgcode-examples",
    publish / skip := true,
    // fork so that the working directory is this project's base directory,
    // examples save their g-code files in out/ relative to it
    run / fork := true
  )
