name := "pdal-jni"

val scala212 = "2.12.21"
val scala213 = "2.13.18"
val scala3 = "3.9.0"
val scalaVersions = Seq(scala213, scala3, scala212)

lazy val commonSettings = Seq(
  scalaVersion := scalaVersions.head,
  crossScalaVersions := scalaVersions,
  organization := "io.pdal",
  description := "PDAL JNI bindings",
  licenses := Seq("BSD" -> uri("https://github.com/PDAL/PDAL/blob/master/LICENSE.txt")),
  homepage := Some(uri("https://www.pdal.io")),
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
      uri("https://github.com/pomadchin")
    )
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings*)
  .settings(
    scalaVersion := scalaVersions.head,
    crossScalaVersions := Nil,
    publish := {},
    publishLocal := {}
  )
  .aggregate(`core-scala`, core, native)

lazy val `core-scala` = project
  .settings(commonSettings*)
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
  .dependsOn(Environment.dependOnNative(native % Runtime)*)

lazy val core = project
  .settings(commonSettings*)
  .settings(name := "pdal")
  .settings(javah / target := (native / nativeCompile / sourceDirectory).value / "include")
  .settings(sbtJniCoreScope := Compile)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.scalaTest % Test,
      Dependencies.circe("parser") % Test
    )
  )
  .dependsOn(Environment.dependOnNative(native % Runtime)*)

lazy val native = project
  .settings(commonSettings*)
  .settings(crossPaths := false)
  .settings(name := "pdal-native")
  .settings(nativeCompile / sourceDirectory := sourceDirectory.value)
  // keep native binaries at native/target/native/<platform> (sbt 2 moved target under target/out)
  .settings(nativeCompile / target := baseDirectory.value / "target" / "native" / nativePlatform.value)
  .settings(
    Compile / unmanagedPlatformDependentNativeDirectories := Seq(
      "x86_64-linux" -> baseDirectory.value / "target/native/x86_64-linux/bin/",
      "x86_64-darwin" -> baseDirectory.value / "target/native/x86_64-darwin/bin/",
      "arm64-darwin" -> baseDirectory.value / "target/native/arm64-darwin/bin/"
    )
  )
  .settings(artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
    artifact.name + "-" + nativePlatform.value + "-" + module.revision + "." + artifact.extension
  })
  .enablePlugins(JniNative, JniPackage)
