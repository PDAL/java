#!/usr/bin/env bash

./scripts/crosscompile-linux.sh && \
sbt native/publishLocal
