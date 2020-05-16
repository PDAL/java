FROM pdal/pdal:2.1
MAINTAINER Grigory Pomadchin <daunnc@gmail.com>

RUN set -ex && \
    apt update -y && \
    apt install \
      openjdk-8-jdk \
      ca-certificates-java -y

ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk-amd6
RUN update-alternatives --set java `update-alternatives --list java | grep java-8`

RUN apt-get -y install bash gcc g++ cmake wget
