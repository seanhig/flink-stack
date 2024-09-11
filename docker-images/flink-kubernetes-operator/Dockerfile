FROM apache/flink-kubernetes-operator

USER root
RUN apt-get update && apt-get install -y awscli maven wget

WORKDIR /
RUN wget https://dlcdn.apache.org/hadoop/common/hadoop-2.10.2/hadoop-2.10.2.tar.gz
RUN tar -xvf *.gz
RUN mv hadoop-2.10.2 hadoop
RUN rm *.gz
#ENV HADOOP_CLASSPATH=/hadoop/etc/hadoop:/hadoop/share/hadoop/common/lib/*:/hadoop/share/hadoop/common/*:/hadoop/share/hadoop/hdfs:/hadoop/share/hadoop/hdfs/lib/*:/hadoop/share/hadoop/hdfs/*:/hadoop/share/hadoop/mapreduce/*:/hadoop/share/hadoop/yarn:/hadoop/share/hadoop/yarn/lib/*:/hadoop/share/hadoop/yarn/*
ENV HADOOP_CLASSPATH=/hadoop/etc/hadoop:/hadoop/share/hadoop/common/lib/*:/hadoop/share/hadoop/common/*:/hadoop/share/hadoop/hdfs:/hadoop/share/hadoop/hdfs/lib/*:/hadoop/share/hadoop/hdfs/*:/hadoop/share/hadoop/mapreduce/*:

RUN echo "Building Flink image with Hadoop and all common dependencies in the /opt/flink/operator-lib folder..."

RUN mkdir /maven
ADD ./maven/pom.xml /maven
ADD update-jars.sh /maven

WORKDIR /maven
RUN chmod +x /maven/update-jars.sh
RUN ./update-jars.sh

USER flink

