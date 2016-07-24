name := "libGcode"

organization := "io.github.dzufferey"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions in Compile ++= Seq(
    //"-Xlog-implicits",
    "-unchecked",
    "-deprecation",
    "-feature",
    "-Xmax-classfile-name", "110"
)

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "0.3.7",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test",
  "io.github.dzufferey" %% "misc-scala-utils" % "0.1-SNAPSHOT"
)

addCompilerPlugin("org.psywerx.hairyfotr" %% "linter" % "0.1.13")

fork := true

resolvers +=  "dzufferey maven repo" at "https://github.com/dzufferey/my_mvn_repo/raw/master/repository"

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

