#!/usr/bin/env bash

## Script to run cross compilation on Mac OS host machine
## The result of this script would be an jar of a proper version
## Which contains native bindings for Linux (64 bit) and Mac Os (64 bit)

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

./scripts/crosscompile.sh "$@"
./scripts/pack-native.sh "$@"
./scripts/merge-native.sh "$@"
