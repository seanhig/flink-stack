ARG exclude_deps=false

FROM flink:1.20.0-scala_2.12 AS base

RUN apt-get update && apt-get install -y awscli maven wget

WORKDIR /
RUN wget https://dlcdn.apache.org/hadoop/common/hadoop-2.10.2/hadoop-2.10.2.tar.gz
RUN tar -xvf *.gz
RUN mv hadoop-2.10.2 hadoop
RUN rm *.gz
#ENV HADOOP_CLASSPATH=/hadoop/etc/hadoop:/hadoop/share/hadoop/common/lib/*:/hadoop/share/hadoop/common/*:/hadoop/share/hadoop/hdfs:/hadoop/share/hadoop/hdfs/lib/*:/hadoop/share/hadoop/hdfs/*:/hadoop/share/hadoop/mapreduce/*:/hadoop/share/hadoop/yarn:/hadoop/share/hadoop/yarn/lib/*:/hadoop/share/hadoop/yarn/*
ENV HADOOP_CLASSPATH=/hadoop/etc/hadoop:/hadoop/share/hadoop/common/lib/*:/hadoop/share/hadoop/common/*:/hadoop/share/hadoop/hdfs:/hadoop/share/hadoop/hdfs/lib/*:/hadoop/share/hadoop/hdfs/*:/hadoop/share/hadoop/mapreduce/*:


FROM base AS branch-exclude-deps-true
RUN echo "Building pure Flink image with only Hadoop..."

FROM base AS branch-exclude-deps-false
RUN echo "Building Flink image with Hadoop and all common dependencies in the /opt/flink/lib/stack folder..."

RUN mkdir /maven
ADD ./maven/pom.xml /maven
ADD update-jars.sh /maven

WORKDIR /maven
RUN mkdir /opt/flink/lib/stack
RUN chmod +x /maven/update-jars.sh
RUN ./update-jars.sh

FROM branch-exclude-deps-${exclude_deps} AS final

WORKDIR /opt/flink

