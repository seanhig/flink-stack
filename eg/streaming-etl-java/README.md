# Streaming ETL Example in Java

This maven project implements the [Flink SQL Streaming ETL to Iceberg](../streaming-etl-to-iceberg/) example using the Java based `Table API`.  See the [EnrichedOrders](src/main/java/io/idstudios/flink/jobs/EnrichedOrders.java) class for details.

> TODO: At the present time there is a lot of hard coded values in the source that will hopefully over time find themselves re-implemented with appropriate configuration mechanisms for deploying secrets to K8s based Flink jobs.