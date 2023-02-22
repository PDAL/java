#!/usr/bin/env bash

# Linux
docker run -it --rm \
  -v $PWD:/pdal-java \
  -v $HOME/.ivy2:/root/.ivy2 \
  -v $HOME/.sbt:/root/.sbt \
  -v $HOME/.coursier/cache:/root/.cache/coursier \
 daunnc/pdal-ubuntu:2.5.1 bash -c "cd ./pdal-java; ./sbt native/compile"
