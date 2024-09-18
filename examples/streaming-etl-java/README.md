# Streaming ETL Example in Java

This maven project implements the [Flink SQL Streaming ETL to Iceberg](../streaming-etl-to-iceberg/) example using the Java based `Table API`.  See the [EnrichedOrders](src/main/java/io/idstudios/flink/jobs/EnrichedOrders.java) class for details.

> TODO: At the present time there is a lot of hard coded values in the source that will hopefully over time find themselves re-implemented with appropriate configuration mechanisms for deploying secrets to K8s based Flink jobs.

## Requirements

- A running `flink-stack` and a completed [streaming-etl-to-iceberg](../streaming-etl-to-iceberg/) example (at the least the MySQL and PostgreSQL tables and data must be in place).
- JDK 11 or higher
- Maven 3+

> Visual Studio Code with the Java Lang Pack was used as the IDE.

## Building the Flink Job Jar

```
mvn clean compile package
```

This will produce the `enriched-orders-jobs-1.0.0.jar` in the `target` folder.

This `job jar` can be uploaded to the `flink-stack` via the `Subnit New Job` feature of the  [Job Manager UI](http://localhost:8081).  Once the jar is uploaded, the `Enriched Orders` job can be `Submitted`.

For the [Kubernetes](../k8s/) deployment example this is automated by building the job jar into the `Flink` image, which is likely the best immutable approach for production deployment.

## Job Configuration and Secrets
The ideal operating environment would be Flink on K8s using the operator.  In this environment it is common to use ENV vars for configuration items, but that wouldn't suit the development model in Flink.

After some consideration Java `.properties` files seemed to be the best option.  However the values in the `.properties` file can be overridden by ENVIRONMENT variables at runtime.

The [JobConfig](src/main/java/io/idstudios/flink/jobs/JobConfig.java) class implements this override using the following formula:

```
    String envKey = key.replace(".", "_").toUpperCase();

```

The `enriched-orders-jobs.properties` file is bundled into the custom `Flink` docker container for k8s operator deployment, but secret values are then overriden by ENVIRONMENT variables.  

> Note: The default location of the `.properties` file is set to the k8s job-image location.  When running in `docker-compose` manually submitted jobs can override this location with a [program argument](#manual-deployment-config-path)

## Secrets
These are best passed using ENVIRONMENT variables, both locally and especially for Kubernetes deployment.

The `docker compose` has these defined in the `.env` file.  For `k8s` this is part of the `Flink Job YAML spec`.

This follows an immutable deployment model where configuration updates would entail new versions be deployed and aligns well with Kubernetes best practice.

## Manual Deployment Config Path
When manually pushing the `job jar` up to the local `flink-stack` docker compose environment, which is handy for testing and development, pass the following parameter as the `program arguments`:

```
--config-filepath /host/examples/k8s/job-jar-image/enriched-orders-jobs.properties
```

> This references the same properties file used for k8s, as the host is a mapped folder on the docker compose image instance for flink task manager and job manager.
