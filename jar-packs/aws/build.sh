mvn clean package
rm ../flink-stack-aws.jar
cp target/flink-stack-aws-1.0-jar-with-dependencies.jar ../flink-stack-aws.jar
rm -rf target