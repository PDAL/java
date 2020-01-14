#!/usr/bin/env bash

# --suffix: sets the suffix you want to publish lib with

for i in "$@"
do
    case $i in
        --suffix=*)
            PDAL_VERSION_SUFFIX="${i#*=}"
            shift
            ;;
        *)
            ;;
    esac
done

export PDAL_VERSION_SUFFIX=${PDAL_VERSION_SUFFIX-"-SNAPSHOT"}

# Linux
docker run -it --rm \
  -v $PWD:/pdal-java \
  -v $HOME/.ivy2:/root/.ivy2 \
  -v $HOME/.sbt:/root/.sbt \
 daunnc/pdal-debian:2.0.0 bash -c "cd ./pdal-java; ./scripts/pack-native.sh --suffix=${PDAL_VERSION_SUFFIX}"

# docker run -it --rm \
#   -v $PWD:/workdir \
#   -v $HOME/.ivy2:/root/.ivy2 \
#   -v $HOME/.sbt:/root/.sbt \
#  daunnc/crossbuild-pdal:latest bash -c "./scripts/pack-native.sh --suffix=${PDAL_VERSION_SUFFIX}"

# Apple cross compilation
# docker run -it --rm -v $PWD:/workdir -e CROSS_TRIPLE=x86_64-apple-darwin daunnc/crossbuild-pdal:latest ./sbt "project native" nativeCompile

# Windows cross compilation
# docker run -it --rm -v $PWD:/workdir -e CROSS_TRIPLE=win64 daunnc/crossbuild-pdal:latest ./sbt "project native" nativeCompile