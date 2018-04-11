#!/usr/bin/env bash

if [ `echo $TRAVIS_SCALA_VERSION | cut -f1-2 -d "."` = "2.11" ]; then
    .travis/test.sh;
else
    .travis/test-212.sh;
fi
