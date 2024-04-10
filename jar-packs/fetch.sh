ICEBERG_VERSION=1.5.0
MAVEN_URL=https://repo1.maven.org/maven2
ICEBERG_MAVEN_URL=$MAVEN_URL/org/apache/iceberg

wget $ICEBERG_MAVEN_URL/iceberg-flink-runtime-1.16/$ICEBERG_VERSION/iceberg-flink-runtime-1.16-$ICEBERG_VERSION.jar

wget $ICEBERG_MAVEN_URL/iceberg-aws-bundle/$ICEBERG_VERSION/iceberg-aws-bundle-$ICEBERG_VERSION.jar