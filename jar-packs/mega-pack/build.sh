mvn clean package
rm ../flink-stack-mega-pack.jar
cp target/flink-stack-mega-pack-1.0-jar-with-dependencies.jar ../flink-stack-mega-pack.jar
rm -rf target