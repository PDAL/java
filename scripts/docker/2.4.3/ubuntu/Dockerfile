FROM pdal/pdal:2.4.3
LABEL Grigory Pomadchin <daunnc@gmail.com>

ENV DEBIAN_FRONTEND noninteractive

RUN set -ex && \
    apt update -y && \
    apt install --no-install-recommends -y openjdk-8-jdk

ENV JAVA_HOME /usr/lib/jvm/java-1.8.0-openjdk-amd6
RUN update-alternatives --set java `update-alternatives --list java | grep java-8`

RUN apt-get -y install bash gcc g++ cmake wget unzip gpg

RUN ln -s /opt/conda/envs/pdal/include/pdal /usr/include/pdal && \
    ln -s /usr/include /usr/lib/include && \
    ln -s /opt/conda/envs/pdal/lib /usr/lib/lib && \
    ln -s /opt/conda/envs/pdal/share/* /usr/share/* || true && \
    ln -s /opt/conda/envs/pdal/lib/* /usr/lib/* || true
