FROM pdal/pdal:1.6-ubuntu
MAINTAINER Grigory Pomadchin <daunnc@gmail.com>

# Install purged deps
RUN apt-get update; \
    apt-get install -y --fix-missing --no-install-recommends \
            build-essential \
            ca-certificates \
            cmake \
            curl \
            gfortran \
            python-software-properties \
            software-properties-common \
            wget \
            automake \
            clang

# Install Java
RUN echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
    add-apt-repository -y ppa:webupd8team/java && \
    apt-get update && \
    apt-get install -y oracle-java8-installer && \
    rm -rf /var/lib/apt/lists/* && \
    rm -rf /var/cache/oracle-jdk8-installer

ENV JAVA_HOME /usr/lib/jvm/java-8-oracle
