# WebOrders Kafka Job Processor

## Requirements

- A running `flink-stack` and a completed [streaming-etl-to-iceberg](../streaming-etl-to-iceberg/) example (at the least the MySQL and PostgreSQL tables and data must be in place).  
- JDK 11 or higher
- Maven 3+

> For this project Visual Studio Code with the RedHat Java Lang Pack was used as the IDE.

## Building the Flink Job Jar

```
mvn clean package
```

This will produce the `weborders-jobs-1.0.0.jar` in the `target` folder.

## Manual Deployment Config Path
When manually pushing the `job jar` up to the local `flink-stack` docker compose environment (for testing and development), pass the following parameter as the `program arguments`:

```
--config-filepath /host/examples/kafka/weborder-jobs/weborder-jobs.properties
```

> for K8s deployment we use a ConfigMap to inject the properties file into the Flink Job container.