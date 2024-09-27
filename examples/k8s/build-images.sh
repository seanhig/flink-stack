docker build -t idstudios/flink-session-cluster:1.20 ./job-image

cp ../kafka/weborder-jobs/target/weborder-jobs-1.0.0.jar ./job-jar-image
cp ../streaming-etl-java/target/enriched-orders-jobs-1.0.0.jar ./job-jar-image
docker build -t idstudios/flink-jobjars:1.1 ./job-jar-image