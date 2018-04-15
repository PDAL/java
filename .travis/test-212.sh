#!/usr/bin/env bash

./sbt "-212" "project core" test || { exit 1; }
./sbt "-212" "project core-scala" test || { exit 1; }

cd examples/pdal-jni
./sbt "-212" "runMain com.azavea.Main" || { exit 1; }
./sbt "-212" "runMain com.azavea.MainScala" || { exit 1; }
cd ~-
