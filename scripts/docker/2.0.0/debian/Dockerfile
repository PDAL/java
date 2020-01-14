FROM debian:sid
MAINTAINER Grigory Pomadchin <daunnc@gmail.com>

RUN set -ex && \
    apt update -y && \
    apt install \
      openjdk-8-jdk \
      ca-certificates-java -y

ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk-amd64

# https://tracker.debian.org/pkg/pdal
RUN apt-get -y install pdal libpdal-dev bash gcc g++ cmake
RUN apt-get -y install wget
