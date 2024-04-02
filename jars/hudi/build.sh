mvn clean package
rm ../flink-hudi-uber.jar
cp target/flink-hudi-uber-1.0-jar-with-dependencies.jar ../flink-hudi-uber.jar
rm -rf target