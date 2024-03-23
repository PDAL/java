val scala212 = "2.12.18"
val scala213 = "2.13.12"
val scala3   = "3.3.1"
val scalaVersions = Seq(scala3, scala213, scala212)

name := "pdal-jni"
version := "0.1.0-SNAPSHOT"
scalaVersion := scala213
crossScalaVersions := Seq(scala3, scala213, scala212)
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

resolvers ++= Resolver.sonatypeOssRepos("releases") ++ Resolver.sonatypeOssRepos("snapshots")

fork := true

val pdalVersion = "2.7.0"

libraryDependencies ++= Seq(
  "io.pdal" %% "pdal"        % pdalVersion,
  "io.pdal" %% "pdal-scala"  % pdalVersion,
  "io.pdal" %  "pdal-native" % pdalVersion,
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)
