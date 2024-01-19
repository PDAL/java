#!/usr/bin/env bash

PDAL_DEPEND_ON_NATIVE=false sbt ";+core;publishSigned;+core-scala/publishSigned"
