#!/usr/bin/env bash

PDAL_DEPEND_ON_NATIVE=false sbt ";+core/publishLocal;+core-scala/publishLocal"
