mvn clean package
rm ../flink-stack-iceberg.jar
cp target/flink-stack-iceberg-1.0-jar-with-dependencies.jar ../flink-stack-iceberg.jar
rm -rf target