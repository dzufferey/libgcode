name := "libGcode"

organization := "io.github.dzufferey"

version := "0.1-SNAPSHOT"

scalaVersion := "2.13.2"

scalacOptions in Compile ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature"
)


libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "2.2.2",
  "org.scalatest" %% "scalatest" % "3.1.1" % "test",
  "io.github.dzufferey" %% "misc-scala-utils" % "0.1-SNAPSHOT",
  "io.github.dzufferey" %% "almond-x3dom-model-viewer" % "0.1-SNAPSHOT"
)

//addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1.17")

fork := true

resolvers ++= Seq(
  "jitpack" at "https://jitpack.io",
  "dzufferey maven repo" at "https://github.com/dzufferey/my_mvn_repo/raw/master/repository"
)

/*
publishMavenStyle := true

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

pomExtra :=
  <licenses>
    <license>
      <name>Apache 2</name>
      <url>https://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
*/
