FROM multiarch/crossbuild:latest
MAINTAINER Grigory Pomadchin <daunnc@gmail.com>

RUN set -ex && \
    echo 'deb http://deb.debian.org/debian jessie-backports main' \
      > /etc/apt/sources.list.d/jessie-backports.list && \

    apt update -y && \
    apt install -t \
      jessie-backports \
      openjdk-8-jdk \
      ca-certificates-java -y

ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk-amd64

RUN echo 'deb http://ftp.us.debian.org/debian/ unstable main contrib non-free' > /etc/apt/sources.list.d/debian-unstable.list

# https://tracker.debian.org/pkg/pdal
RUN apt-get update && apt-get -y install pdal libpdal-dev bash
