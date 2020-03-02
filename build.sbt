name := "pdal-jni"

lazy val commonSettings = Seq(
  version := "2.1.1" + Environment.versionSuffix,
  scalaVersion := "2.13.1",
  crossScalaVersions := Seq("2.13.1", "2.12.10", "2.11.12"),
  organization := "io.pdal",
  description := "PDAL JNI bindings",
  licenses := Seq("BSD" -> url("https://github.com/PDAL/PDAL/blob/master/LICENSE.txt")),
  homepage := Some(url("http://www.pdal.io")),
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
  assembly / test := {},
  shellPrompt := { s => Project.extract(s).currentProject.id + " > " },
  commands ++= Seq(
    Commands.processJavastyleCommand("publish"),
    Commands.processJavastyleCommand("publishSigned")
  ),
  Test / publishArtifact := false,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <scm>
      <url>git@github.com:PDAL/PDAL.git</url>
      <connection>scm:git:git@github.com:PDAL/PDAL.git</connection>
    </scm>
      <developers>
        <developer>
          <id>pomadchin</id>
          <name>Grigory Pomadchin</name>
          <url>http://github.com/pomadchin/</url>
        </developer>
      </developers>
    ),
  Global / PgpKeys.useGpg := true,
  Global / PgpKeys.gpgCommand := "gpg"
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .aggregate(`core-scala`, core, native)

lazy val `core-scala` = project
  .settings(commonSettings: _*)
  .settings(Dependencies.macroSettings)
  .settings(Dependencies.licenseSettings)
  .settings(name := "pdal-scala")
  .settings(javah / target := (native / nativeCompile / sourceDirectory).value / "include")
  .settings(libraryDependencies ++= Seq(
    Dependencies.circe("core").value,
    Dependencies.circe("generic").value,
    Dependencies.circe("generic-extras").value,
    Dependencies.circe("parser").value,
    Dependencies.jtsCore,
    Dependencies.scalaTest % Test
  ))
  .dependsOn(core)
  .dependsOn(Environment.dependOnNative(native % Runtime): _*)

lazy val core = project
  .settings(commonSettings: _*)
  .settings(name := "pdal")
  .settings(javah / target := (native / nativeCompile / sourceDirectory).value / "include")
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
