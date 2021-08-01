name := "pdal-jni"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.13.6"
crossScalaVersions := Seq("2.13.6", "2.12.14")
organization := "com.azavea"
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:higherKinds",
  "-language:postfixOps",
  "-language:existentials",
  "-feature"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
 )

fork := true

val pdalVersion = "0.0.0+98-727bef09-SNAPSHOT"

libraryDependencies ++= Seq(
  "io.pdal" %% "pdal"        % pdalVersion,
  "io.pdal" %% "pdal-scala"  % pdalVersion,
  "io.pdal" %  "pdal-native" % pdalVersion,
  "org.scalatest" %% "scalatest" % "3.2.9" % Test
)
