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

cd ./native/target
rm -f ./pdal-native-2.2.0${PDAL_VERSION_SUFFIX}.jar
rm -rf ./tmp; mkdir -p ./tmp

cd tmp; jar -xf ../pdal-native-x86_64-darwin-2.2.0${PDAL_VERSION_SUFFIX}.jar; cd ~-
cd tmp; jar -xf ../pdal-native-x86_64-linux-2.2.0${PDAL_VERSION_SUFFIX}.jar; cd ~-

jar -cvf pdal-native-2.1.6${PDAL_VERSION_SUFFIX}.jar -C tmp .

cd ./tmp

