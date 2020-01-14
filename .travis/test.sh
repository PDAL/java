#!/usr/bin/env bash

./sbt "project core" test || { exit 1; }
./sbt "project core-scala" test || { exit 1; }

cd examples/pdal-jni
./sbt "runMain com.azavea.Main" || { exit 1; }
./sbt "runMain com.azavea.MainScala" || { exit 1; }
cd ~-
