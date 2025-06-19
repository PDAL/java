# PDAL Java Bindings

[![CI](https://github.com/PDAL/java/workflows/CI/badge.svg)](https://github.com/PDAL/java/actions) [![Join the chat at https://gitter.im/PDAL/PDAL](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/PDAL/PDAL?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Maven Central](https://img.shields.io/maven-central/v/io.pdal/pdal_2.13)](https://search.maven.org/search?q=g:io.pdal) [![Snapshots Badge](https://img.shields.io/badge/snapshots-available-orange)](https://central.sonatype.com/service/rest/repository/browse/maven-snapshots/io/pdal/pdal_3/) 

Java bindings to use PDAL on JVM (supports PDAL >= 2.0). macOS users can experience some issues with bindings that were build against a different PDAL version, so try to use a consistent PDAL version. 

It is released independently from PDAL itself as of PDAL 1.7.

 See [https://pdal.io/java.html](https://pdal.io/java.html) for more info.


## Table of Contents
- [Usage](#usage)
- [Examples](#examples)
- [Build](#build) 
- [Possible issues and solutions](#possible-issues-and-solutions)
- [How To Release](#how-to-release)

## Usage 
You can use `pdal-native` dep published into maven central in case you don't have installed JNI bindings and to avoid steps described below.
Dependency contains bindings for `arm64-darwin`, and `x86_64-linux`, other versions are not supported yet. We dropped `x86_64-darwin` as of the PDAL Java `2.8.0` release.

### Versioning scheme

Given a `{major}.{minor}.{patch}` version:
* `{major}.{minor}` - matches the **PDAL** version it is published for
  * e.g. **pdal-java** of version `2.6.1` is suitable for all **PDAL** versions `2.6.x` (`2.6.0`, ..., `2.6.3`, etc) major = `2`, minor = `6`
* `{patch}` - this portion of the version corresponds to updates within **pdal-java** and should remain compatible with **PDAL library** `{major}.{minor}` versions
  * This implies that there may be multiple **pdal-java** releases for the same **PDAL library** version. All releases are compatible with the matching **PDAL library** `{major}.{minor}` version. Thus, higher patch versions are to be preferred.

### Using PDAL JNI With SBT
```scala
// pdal is published to maven central, the snapshot repository can be added in addition
resolvers += Resolver.sonatypeCentralSnapshots // for snaphots
// `<latest version>` refers to the version indicated by the badge above
libraryDependencies ++= Seq(
  "io.pdal" %% "pdal" % "<latest version>", // core library
  "io.pdal" %  "pdal-native" % "<latest version>" // jni bindings
)
```

If you would like to use your own bindings, it is necessary to set `java.library.path`:

```scala
// macOS X example with manual JNI installation
// cp -f native/target/resource_managed/main/native/arm64-darwin/libpdaljni.2.8.dylib /usr/local/lib/libpdaljni.2.8.dylib
// place built binary into /usr/local/lib, and pass java.library.path to your JVM
javaOptions += "-Djava.library.path=/usr/local/lib"
```


### PDAL-Scala (Scala 2.x)
Scala API allows to build pipeline expressions instead of writing a raw JSON.

```scala
// `<latest version>` refers to the version indicated by the badge above
libraryDependencies ++= Seq(
  "io.pdal" %% "pdal-scala" % "<latest version>", // scala core library
  "io.pdal" %  "pdal-native" % "<latest version>" // jni bindings
)
```

Scala API covers PDAL 2.0.x, to use any custom DSL that is not covered by the
current Scala API you can use `RawExpr` type to build `Pipeline Expression`.


## Examples
#### Demo project with examples

JNI bindings basic usage examples can be found [here](./examples).

### PDAL Core (Scala 2.x / 3.x)
```scala
import io.pdal._

// pipeline definition
val json =
  """
     |{
     |  "pipeline" : [
     |    {
     |      "filename" : "/path/to/las",
     |      "type" : "readers.las"
     |    },
     |    {
     |      "type" : "filters.crop"
     |    },
     |    {
     |      "filename" : "/path/to/new/las",
     |      "type" : "writers.las"
     |    }
     |  ]
     |}
  """.stripMargin

val pipeline = Pipeline(json, LogLevel.Debug5) // initialize and make it really noisy

pipeline.execute() // execute the pipeline

val metadata = pipeline.getMetadata() // retrieve metadata
val pvs      = pipeline.getPointViews() // iterator over PointViews
val pv       = pvs.next() // let's take the first PointView

// load all points into JVM memory
// PointCloud provides operations on PDAL points that
// are loaded in this case into JVM memory as a single Array[Byte]
val pointCloud = pv.getPointCloud()
val x = pointCloud.getDouble(0, DimType.X) // get a point with PointId = 0 and only a single dimensions

// in some cases it is not neccesary to load everything into JVM memory
// so it is possible to get only required points directly from the PointView
val y = pv.getDouble(0, DimType.Y)

// it is also possible to get access to the triangular mesh generated via PDAL
val mesh = pv.getTriangularMesh()
// the output is an Array of Triangles
// Each Triangle contains PointIds from the PDAL point table
val triangles = mesh.asArray

pv.close()
pvs.close()
pipeline.close()
```

### PDAL Core (Java)

```java
import io.pdal.*;

// pipeline definition
String json =
  """
      {
        "pipeline" : [
          {
            "filename" : "/path/to/las",
            "type" : "readers.las"
          },
          {
            "type" : "filters.crop"
          },
          {
            "filename" : "/path/to/new/las",
            "type" : "writers.las"
          }
        ]
      }
  """;

var pipeline = new Pipeline(json, LogLevel.Debug5()); // initialize and make it really noisy

pipeline.execute(); // execute the pipeline

var metadata = pipeline.getMetadata(); // retrieve metadata
var pvs      = pipeline.getPointViews(); // iterator over PointViews
var pv       = pvs.next(); // let's take the first PointView

// load all points into JVM memory
// PointCloud provides operations on PDAL points that
// are loaded in this case into JVM memory as a single Array[Byte]
var pointCloud = pv.getPointCloud();
var x = pointCloud.getDouble(0, DimType.X()); // get a point with PointId = 0 and only a single dimensions

// in some cases it is not neccesary to load everything into JVM memory
// so it is possible to get only required points directly from the PointView
var y = pv.getDouble(0, DimType.Y());

// it is also possible to get access to the triangular mesh generated via PDAL
var mesh = pv.getTriangularMesh();
// the output is an Array of Triangles
// Each Triangle contains PointIds from the PDAL point table
var triangles = mesh.asArray();

pv.close();
pvs.close();
pipeline.close();
```

### PDAL Scala

```scala
import io.pdal._
import io.pdal.pipeline._

// To construct the expected json
val expected =
  """ 
      |{
      |  "pipeline" : [
      |    {
      |      "filename" : "/path/to/las",
      |      "type" : "readers.las"
      |    },
      |    {
      |      "type" : "filters.crop"
      |    },
      |    {
      |      "filename" : "/path/to/new/las",
      |      "type" : "writers.las"
      |    }
      |  ]
      |}
  """.stripMargin
// The same, but using scala DSL
val pc = ReadLas("/path/to/las") ~ FilterCrop() ~ WriteLas("/path/to/new/las")

// The same, but using RawExpr, to support not implemented PDAL Pipeline API features
// RawExpr accepts a circe.Json type, which can be a json object of any desired complexity
val pcWithRawExpr = ReadLas("/path/to/las") ~ RawExpr(Map("type" -> "filters.crop").asJson) ~ WriteLas("/path/to/new/las")

// Create Pipelines from the constructed expressions
val pipeline = pc.toPipeline
val pipelineRaw = pcWithRawExpr.toPipline
```

## Build
Development purposes (including binaries) compilation:
1. Install PDAL (using brew / package managers (unix) / build from sources / [Conda](#install-pdal-with-conda) / etc) 
2. Install sbt (using brew / package managers (unix)) (only after `v2.4.x`)
3. Build native libs `sbt native/nativeCompile` (optionally, binaries would be built during tests run) or `sbt native/publishLocal` for the built jar only
4. Run `sbt core/test` to run PDAL tests

Only Java development purposes compilation:
1. Provide `$LD_LIBRARY_PATH` or `$DYLD_FALLBACK_LIBRARY_PATH`
2. If you don't want to provide global variable you can pass `-Djava.library.path=<path>` into sbt:
`./sbt -Djava.library.path=<path>`
3. Set `PDAL_DEPEND_ON_NATIVE=false` (to disable `native` project build)
4. Run `PDAL_DEPEND_ON_NATIVE=false sbt`
Finally the possible command to launch and build PDAL JNI bindings could be:
```bash
# Including binaries build
sbt
```
```bash
# Java side development without binaries build
PDAL_DEPEND_ON_NATIVE=false sbt -Djava.library.path=<path>
```
## Possible issues and solutions

#### - In case of not installed as global PDAL change [this](./java/native/src/CMakeLists.txt#L25) line to:
```cmake
set(CMAKE_CXX_FLAGS "$ENV{PDAL_LD_FLAGS} $ENV{PDAL_CXX_FLAGS} -std=c++11")
```
In this case sbt launch would be the following:
```bash
PDAL_LD_FLAGS=`pdal-config --libs` PDAL_CXX_FLAGS=`pdal-config --includes` sbt
```

#### - Sometimes can happen a bad dynamic linking issue (somehow spoiled environment),

the quick workaround would be to replace [this](./java/native/src/CMakeLists.txt#L25) line to:

```cmake
set(CMAKE_CXX_FLAGS "-L<path to dynamic libs> -std=c++11")
```

#### - On macOS could be difficult to install PDAL sometimes (near new releases). You have three options
- ##### Install PDAL with conda 
  Here the [PDAL conda guide](https://pdal.io/en/2.6.0/workshop/conda.html)

- ##### Install PDAL with brew 
  Just run `brew install pdal`

- ##### Build PDAL from sources 
  Follow the [official guide](https://pdal.io/en/latest/development/compilation/index.html#compilation)

#### - When using the Maven Central JARs on macOS, I get `Library not loaded: @rpath/libpdalcpp.16.dylib` error

If you are sure PDAL is correctly installed, you can run `pdal-config --libs` and take the path after the `-L` argument
and assign it to the `DYLD_FALLBACK_LIBRARY_PATH`:

    ❯ pdal-config --libs
    -L/opt/homebrew/Cellar/pdal/2.6.3/lib -lpdalcpp
    ❯ export DYLD_FALLBACK_LIBRARY_PATH=/opt/homebrew/Cellar/pdal/2.6.3/lib
    ❯ java -jar my-pdal-proj.jar

## How To Release
All the instructions related to the local / maven release process are documented in the [HOWTORELEASE.txt](./HOWTORELEASE.txt) file.

For the local publish it is possible to use the following commands:
* `scripts/publish-local.sh` - to publish Scala artifacts
* `scripts/publish-local-native.sh` - to compile and publish artifact with native binaries
For the additional information checkout the [HOWTORELEASE.txt](./HOWTORELEASE.txt) file and the [scripts](./scripts) directory.

