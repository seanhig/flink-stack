# Streaming ETL Example in Java

This maven project implements the [Flink SQL Streaming ETL to Iceberg](../streaming-etl-to-iceberg/) example using the Java based `Table API`.  See the [EnrichedOrdersMySQL](src/main/java/io/idstudios/flink/jobs/EnrichedOrdersMySQL.java) and [EnrichedOrdersIceberg](src/main/java/io/idstudios/flink/jobs/EnrichedOrdersIceberg.java) classes for details.

## Requirements

- A running `flink-stack` and a completed [streaming-etl-to-iceberg](../streaming-etl-to-iceberg/) example (at the least the MySQL and PostgreSQL tables and data must be in place).  
- JDK 11 or higher
- Maven 3+

> For this project Visual Studio Code with the RedHat Java Lang Pack was used as the IDE.

> __Note:__ although it is required to have the [streaming-etl-to-iceberg](../streaming-etl-to-iceberg/) tables in place, the `Flink Jobs` themselves should not be running (cancelled).

## Building the Flink Job Jar

```
mvn clean compile package
```

This will produce the `enriched-orders-jobs-1.0.0.jar` in the `target` folder.

This `job jar` can be uploaded to the `flink-stack` via the `Subnit New Job` feature of the  [Job Manager UI](http://localhost:8081).  Once the jar is uploaded, the `Enriched Orders All` job can be `Submitted`.  This will load all of the EnrichedOrders jobs.

## Job Configuration and Secrets
The ideal operating environment would be Flink on K8s using the operator.  In this environment it is common to use ENV vars for configuration items, but that wouldn't suit the development model in Flink.

After some consideration Java `.properties` files seemed to be the best option.  However the values in the `.properties` file can be overridden by ENVIRONMENT variables at runtime.

The [JobConfig](src/main/java/io/idstudios/flink/jobs/JobConfig.java) class implements this override using the following formula:

```
    String envKey = key.replace(".", "_").toUpperCase();

```

The `enriched-orders-jobs.properties` file is injected into the `Flink Job` docker container instance via a `ConfigMap` for k8s operator deployment, but secret values are then overriden by `ENVIRONMENT` variables in the `FlinkDeployment` yaml.  

> Note: The default location of the `.properties` file is set to the k8s injected path location (and visa versa).  When running in `docker-compose` manually submitted jobs can override this location with a [program argument](#manual-deployment-config-path) (below).

## Secrets
These are best passed using ENVIRONMENT variables, both locally and especially for Kubernetes deployment.

The `docker compose` has these defined in the `.env` file.  For `k8s` this is defined as part of the `FlinkDeployment YAML spec` and the `ENV` values, which reference `K8s secrets` are added to the session cluster job container.

## Manual Deployment Config Path
When manually pushing the `job jar` up to the local `flink-stack` docker compose environment (for testing and development), pass the following parameter as the `program arguments`:

```
--config-filepath /host/examples/streaming-etl-java/enriched-orders-jobs.properties
```

> for K8s deployment we use a ConfigMap to inject the properties file into the Flink Job container.