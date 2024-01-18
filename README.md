# PDAL Java bindings

[![CI](https://github.com/PDAL/java/workflows/CI/badge.svg)](https://github.com/PDAL/java/actions) [![Join the chat at https://gitter.im/PDAL/PDAL](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/PDAL/PDAL?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [![Maven Central](https://img.shields.io/maven-central/v/io.pdal/pdal_2.13)](https://search.maven.org/search?q=g:io.pdal) [![Snapshots](https://img.shields.io/nexus/s/https/oss.sonatype.org/io.pdal/pdal_2.13)](https://oss.sonatype.org/content/repositories/snapshots/io/pdal/)

Java bindings to use PDAL on JVM (supports PDAL >= 2.0).
Mac users can experience some issues with bindings that were build against a different PDAL version,
so try to use a consistent PDAL version.

It is released independently from PDAL itself as of PDAL 1.7.

See [https://pdal.io/java.html](https://pdal.io/java.html) for more info.

## Using PDAL JNI with SBT

```scala
// pdal is published to maven central, but you can use the following repos in addition
resolvers ++= 
  Resolver.sonatypeOssRepos("releases") ++ 
  Resolver.sonatypeOssRepos("snapshots") // for snaphots

// `<latest version>` refers to the version indicated by the badge above
libraryDependencies ++= Seq(
  "io.pdal" %% "pdal" % "<latest version>", // core library
  "io.pdal" %  "pdal-native" % "<latest version>" // jni bindings
)
```

If you would like to use your own bindings, it is necessary to set `java.library.path`:

```scala
// Mac OS X example with manual JNI installation
// cp -f native/target/resource_managed/main/native/x86_64-darwin/libpdaljni.2.1.dylib /usr/local/lib/libpdaljni.2.1.dylib
// place built binary into /usr/local/lib, and pass java.library.path to your JVM
javaOptions += "-Djava.library.path=/usr/local/lib"
```

You can use `pdal-native` dep in case you don't have installed JNI bindings and to avoid steps described above.
Dependency contains bindings for `x86_64-darwin` and `x86_64-linux`, other versions are not supported yet.

## PDAL-Scala (Scala 2.x)

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

### Code examples

```scala
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
```
## Use PDAL inside a JAVA environment
This is an example about how to use pdal inside a pure java environment. 

```java
// Create the expected json String
String expectedJSON =
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
  """
// Decide the verbosity of your output
int logLevel = 0;

// Initialine a Pipeline object
// Be careful, before version v2.5.0 the constructor was 'new Pipeline(String jsonString)'
Pipeline pipeline = new Pipeline(json,3);  

// Initialize the pipeline
pipeline.initialize();

// Execute the pipeline
pipeline.execute();

// Now you can for example extract a PointViewIterator
PointViewIterator pvs = pipeline.getPointViews();
// And a single PointView..
PointView pv = pvs.next();
// Now remember to close the pipeline to avoid a leak of resources
pvs.close();
pipeline.close();
```

### Demo project example

JNI bindings basic usage examples can be found [here](./examples).

## How to compile

Development purposes (including binaries) compilation:
  1. Install PDAL (using brew / package managers (unix) / build from sources / etc)     
  2. Build native libs `./sbt native/nativeCompile` (optionally, binaries would be built during tests run)
  3. Run `./sbt core/test` to run PDAL tests

Only Java development purposes compilation:
  1. Provide `$LD_LIBRARY_PATH` or `$DYLD_LIBRARY_PATH`
  2. If you don't want to provide global variable you can pass `-Djava.library.path=<path>` into sbt:
    `./sbt -Djava.library.path=<path>`
  3. Set `PDAL_DEPEND_ON_NATIVE=false` (to disable `native` project build)
  4. Run `PDAL_DEPEND_ON_NATIVE=false ./sbt`

Finally the possible command to launch and build PDAL JNI bindings could be:

```bash
# Including binaries build
./sbt
```

```bash
# Java side development without binaries build
PDAL_DEPEND_ON_NATIVE=false ./sbt -Djava.library.path=<path>
```

### Possible issues and solutions

1. In case of not installed as global PDAL change [this](./java/native/src/CMakeLists.txt#L25) line to:

  ```cmake
  set(CMAKE_CXX_FLAGS "$ENV{PDAL_LD_FLAGS} $ENV{PDAL_CXX_FLAGS} -std=c++11")
  ```
  In this case sbt launch would be the following:

  ```bash
  PDAL_LD_FLAGS=`pdal-config --libs` PDAL_CXX_FLAGS=`pdal-config --includes` ./sbt
  ```

2. Sometimes can happen a bad dynamic linking issue (somehow spoiled environment),
   the quick workaround would be to replace [this](./java/native/src/CMakeLists.txt#L25) line to:

  ```cmake
  set(CMAKE_CXX_FLAGS "-L<path to dynamic libs> -std=c++11")
  ```

## How to release

All the instructions related to the local / maven release process are documented in the [HOWTORELEASE.txt](./HOWTORELEASE.txt) file.

For the local publish it is possible to use the following commands:

* `scripts/publish-local.sh` - to publish Scala artifacts
* `scripts/publish-local-native.sh` - to compile and publish artifact with native binaries

For the additional information checkout the [HOWTORELEASE.txt](./HOWTORELEASE.txt) file and the [scripts](./scripts) directory.
