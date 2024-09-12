# Streaming ETL Example in Java

This maven project implements the [Flink SQL Streaming ETL to Iceberg](../streaming-etl-to-iceberg/) example using the Java based `Table API`.  See the [EnrichedOrders](src/main/java/io/idstudios/flink/jobs/EnrichedOrders.java) class for details.

> TODO: At the present time there is a lot of hard coded values in the source that will hopefully over time find themselves re-implemented with appropriate configuration mechanisms for deploying secrets to K8s based Flink jobs.

## Requirements

- A running `flink-stack` and a completed [streaming-etl-to-iceberg](../streaming-etl-to-iceberg/) example (at the least the MySQL and PostgreSQL tables and data must be in place).
- JDK 11 or higher
- Maven 3+

> Visual Studio Code with the Java Lang Pack was used as the IDE.

```
mvn clean compile package
```

Will produce the `enriched-orders-job-1.0.0.jar` in the `target` folder.

This `job jar` can be uploaded to the `flink-stack` via the `Subnit New Job` feature of the  [Job Manager UI](http://localhost:8081).  Once the jar is uploaded, the `Enriched Orders` job can be `Submitted`.

For the [Kubernetes](../k8s/) deployment example this is automated by building the job jar into the `Flink` image, which is likely the best immutable approach for production deployment.

