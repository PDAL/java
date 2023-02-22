name := "pdal-jni"

val scala212 = "2.12.17"
val scala213 = "2.13.10"
val scala3   = "3.2.2"
val scalaVersions = Seq(scala3, scala213, scala212)

lazy val commonSettings = Seq(
  scalaVersion := scalaVersions.head,
  crossScalaVersions := scalaVersions,
  organization := "io.pdal",
  description := "PDAL JNI bindings",
  licenses := Seq("BSD" -> url("https://github.com/PDAL/PDAL/blob/master/LICENSE.txt")),
  homepage := Some(url("https://www.pdal.io")),
  versionScheme := Some("semver-spec"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-language:higherKinds",
    "-language:postfixOps",
    "-language:existentials",
    "-feature",
    "-target:jvm-1.8"
  ),
  shellPrompt := { s => Project.extract(s).currentProject.id + " > " },
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
  .settings(
    scalaVersion := scalaVersions.head,
    crossScalaVersions := Nil,
    publish := {},
    publishLocal := {}
  )
  .aggregate(`core-scala`, core, native)

lazy val `core-scala` = project
  .settings(commonSettings: _*)
  .settings(Dependencies.macroSettings)
  .settings(Dependencies.licenseSettings)
  .settings(scalaVersion := scala213, crossScalaVersions := Seq(scala213, scala212))
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
  .settings(libraryDependencies ++= Seq(
    Dependencies.scalaTest % Test,
    Dependencies.circe("parser") % Test
  )
  )
  .dependsOn(Environment.dependOnNative(native % Runtime): _*)

lazy val native = project
  .settings(commonSettings: _*)
  .settings(crossPaths := false)
  .settings(name := "pdal-native")
  .settings(nativeCompile / sourceDirectory := sourceDirectory.value)
  .settings(
    Compile / unmanagedPlatformDependentNativeDirectories := Seq(
      "x86_64-linux"  -> target.value / "native/x86_64-linux/bin/",
      "x86_64-darwin" -> target.value / "native/x86_64-darwin/bin/",
      "arm64-darwin"  -> target.value / "native/arm64-darwin/bin/"
    )
  )
  .settings(artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
    artifact.name + "-" + nativePlatform.value + "-" + module.revision + "." + artifact.extension
  })
  .enablePlugins(JniNative, JniPackage)
