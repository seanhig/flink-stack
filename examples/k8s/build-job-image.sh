cp ../streaming-etl-java/target/enriched-orders-job-1.0.0.jar ./job-image
docker build -t idstudios/flink-enriched-orders:1.19 ./job-image