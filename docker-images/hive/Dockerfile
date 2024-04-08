FROM apache/hive:3.1.3

USER root
RUN apt-get update && apt-get install -y curl 

RUN curl https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/3.4.0/hadoop-aws-3.4.0.jar -o /opt/hive/lib/hadoop-aws-3.4.0.jar && \
    curl https://repo1.maven.org/maven2/software/amazon/awssdk/bundle/2.20.18/bundle-2.20.18.jar -o /opt/hive/lib/bundle-2.20.18.jar && \
    curl https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk-bundle/1.12.648/aws-java-sdk-bundle-1.12.648.jar -o /opt/hive/lib/aws-java-sdk-bundle-1.12.648.jar

RUN curl https://archive.apache.org/dist/db/derby/db-derby-10.14.2.0/db-derby-10.14.2.0-bin.tar.gz -o db-derby-10.14.2.0-bin.tar.gz && \
    tar xf db-derby-10.14.2.0-bin.tar.gz

USER hive


