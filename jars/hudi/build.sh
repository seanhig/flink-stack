mvn clean package
rm ../flink-hudi-uber.jar
cp target/flink-stack-hudi-1.0-jar-with-dependencies.jar ../flink-stack-hudi.jar
rm -rf target