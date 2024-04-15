mvn clean package
rm ../flink-stack-serde.jar
cp target/flink-stack-serde-1.0-jar-with-dependencies.jar ../flink-stack-serde.jar
rm -rf target