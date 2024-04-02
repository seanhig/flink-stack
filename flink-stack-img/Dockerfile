FROM flink:1.19.0-java11

RUN apt-get update && apt-get install -y awscli maven wget

WORKDIR /
RUN wget https://dlcdn.apache.org/hadoop/common/hadoop-3.4.0/hadoop-3.4.0.tar.gz
RUN tar -xvf *.gz
RUN mv hadoop-3.4.0 hadoop
RUN rm *.gz
ENV HADOOP_CLASSPATH=/hadoop/etc/hadoop:/hadoop/share/hadoop/common/lib/*:/hadoop/share/hadoop/common/*:/hadoop/share/hadoop/hdfs:/hadoop/share/hadoop/hdfs/lib/*:/hadoop/share/hadoop/hdfs/*:/hadoop/share/hadoop/mapreduce/*:/hadoop/share/hadoop/yarn:/hadoop/share/hadoop/yarn/lib/*:/hadoop/share/hadoop/yarn/*

RUN mkdir /maven
ADD ./maven/pom.xml /maven
ADD update-jars.sh /maven

WORKDIR /maven
RUN mkdir /opt/flink/lib/stack
RUN chmod +x /maven/update-jars.sh
RUN ./update-jars.sh

WORKDIR /opt/flink
