mvn clean package
rm ../flink-stack-jdbc.jar
cp target/flink-stack-jdbc-1.0-jar-with-dependencies.jar ../flink-stack-jdbc.jar
rm -rf target