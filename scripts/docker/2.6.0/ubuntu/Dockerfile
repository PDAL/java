FROM pdal/pdal:2.6.0
LABEL Grigory Pomadchin <daunnc@gmail.com>

ENV DEBIAN_FRONTEND noninteractive

RUN set -ex && \
    apt update -y && \
    apt install --no-install-recommends -y openjdk-8-jdk

ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk-amd6
RUN update-alternatives --set java `update-alternatives --list java | grep java-8`

RUN apt-get update && apt-get -y install bash gcc g++ cmake wget unzip gpg software-properties-common

# Install SBT
RUN apt-get install -y curl gnupg && \
 echo 'deb https://repo.scala-sbt.org/scalasbt/debian all main' | tee /etc/apt/sources.list.d/sbt.list && \
 echo 'deb https://repo.scala-sbt.org/scalasbt/debian /' | tee /etc/apt/sources.list.d/sbt_old.list && \
 curl -sL 'https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823' | apt-key add - && \
 apt-get update && \
 apt-get install -y sbt

RUN ln -s /opt/conda/envs/pdal/include/pdal /usr/include/pdal && \
    ln -s /usr/include /usr/lib/include && \
    ln -s /opt/conda/envs/pdal/lib /usr/lib/lib && \
    ln -s /opt/conda/envs/pdal/share/* /usr/share/* || true && \
    ln -s /opt/conda/envs/pdal/lib/* /usr/lib/* || true

# GLIBCXX_3.4.30
# strings /usr/lib/x86_64-linux-gnu/libstdc++.so.6 | grep GLIBCXX
RUN apt install -y build-essential
