FROM pdal/pdal:2.3
LABEL Grigory Pomadchin <daunnc@gmail.com>

RUN echo "deb http://deb.debian.org/debian/ sid main" >> "/etc/apt/sources.list.d/openjdk-8.list"

RUN set -ex && \
    mkdir /usr/share/man/man1 && \
    apt update -y && \
    apt install --no-install-recommends -y \
      openjdk-8-jdk

ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk-amd6
RUN update-alternatives --set java `update-alternatives --list java | grep java-8`

RUN apt-get -y install bash gcc g++ cmake wget

RUN ln -s /opt/conda/envs/pdal/include/pdal /usr/include/pdal && \
    ln -s /usr/include /usr/lib/include && \
    ln -s /opt/conda/envs/pdal/lib /usr/lib/lib && \
    ln -s /opt/conda/envs/pdal/share/* /usr/share/* || true && \
    ln -s /opt/conda/envs/pdal/lib/* /usr/lib/* || true
