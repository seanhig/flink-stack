mvn clean package
rm ../flink-stack-hive.jar
cp target/flink-stack-hive-1.0-jar-with-dependencies.jar ../flink-stack-hive.jar
rm -rf target