name := "pdal-jni"

val scalaVersions = Seq("3.0.1", "2.13.6", "2.12.14")

lazy val commonSettings = Seq(
  scalaVersion := scalaVersions.head,
  crossScalaVersions := scalaVersions,
  organization := "io.pdal",
  description := "PDAL JNI bindings",
  licenses := Seq("BSD" -> url("https://github.com/PDAL/PDAL/blob/master/LICENSE.txt")),
  homepage := Some(url("https://www.pdal.io")),
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false },
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-language:higherKinds",
    "-language:postfixOps",
    "-language:existentials",
    "-feature"
  ),
  shellPrompt := { s => Project.extract(s).currentProject.id + " > " },
  commands ++= Seq(
    Commands.processJavastyleCommand("publish"),
    Commands.processJavastyleCommand("publishSigned")
  ),
  Test / publishArtifact := false,
  developers := List(
    Developer(
      "pomadchin",
      "Grigory Pomadchin",
      "@pomadchin",
      url("https://github.com/pomadchin")
    )
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(addCommandAlias("test-all", ";+core/test;+core-scala/test"))
  .aggregate(`core-scala`, core, native)

lazy val `core-scala` = project
  .settings(commonSettings: _*)
  .settings(Dependencies.macroSettings)
  .settings(Dependencies.licenseSettings)
  .settings(scalaVersion := "2.13.6", crossScalaVersions := Seq("2.13.6", "2.12.14"))
  .settings(name := "pdal-scala")
  .settings(javah / target := (native / nativeCompile / sourceDirectory).value / "include")
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.circe("core"),
      Dependencies.circe("generic"),
      Dependencies.circe("generic-extras"),
      Dependencies.circe("parser"),
      Dependencies.jtsCore,
      Dependencies.scalaTest % Test
    )
  )
  .dependsOn(core)
  .dependsOn(Environment.dependOnNative(native % Runtime): _*)

lazy val core = project
  .settings(commonSettings: _*)
  .settings(name := "pdal")
  .settings(javah / target := (native / nativeCompile / sourceDirectory).value / "include")
  .settings(sbtJniCoreScope := Compile)
  .settings(libraryDependencies += Dependencies.scalaTest % Test)
  .dependsOn(Environment.dependOnNative(native % Runtime): _*)

lazy val native = project
  .settings(commonSettings: _*)
  .settings(crossPaths := false)
  .settings(name := "pdal-native")
  .settings(nativeCompile / sourceDirectory := sourceDirectory.value)
  .settings(artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
    artifact.name + "-" + nativePlatform.value + "-" + module.revision + "." + artifact.extension
  })
  .enablePlugins(JniNative, JniPackage)
