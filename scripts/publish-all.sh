#!/usr/bin/env bash

./scripts/publish.sh "$@"
./scripts/publish-212.sh "$@"
./scripts/publish-211.sh "$@"
./scripts/publish-javastyle.sh "$@"
./scripts/publish-native.sh "$@"
