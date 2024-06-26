# Flink Stack

A `docker-compose` stack for Flink and Flink-SQL.

The primary focus of this `Flink Stack` is to support CDC to an AWS S3 Iceberg Data Lake in real time, enabling Athena and the Data Lake to act as a severless analytical datastore.  

![Flink Stack Overview](docs/images/Lake-flink-stack-quick.png)

The `Flink Stack` can handle moving data between source systems using CDC, as well as migrating that data to Iceberg on S3 in real time.  Everything can be managed through the `Fink SQL Client` using existing connectors and does not require custom Java development.

The `Flink Stack` can perform both streaming and batch ETL.

`CDC Sources` can be joined across systems to create real-time consolidated views, which can then be replicated to Apache Iceberg on S3, also in real-time.  `Iceberg` will update the `Glue Catalog` to enable Athena to read results in real time.

`Apache Iceberg` stores all change over time allowing for custom queries that show record change over time, or point in time values.

![Flink Stack Full](docs/images/Lake-flink-stack-full.png)


The `Flink Stack` includes:

- Minio for local S3 object storage
- Hive Metastore as a persistent Data Catalog
- Zookeeper for HA and job state 
- Flink CDC Connectors and [Jar Packs](./jar-packs)
- Integration of Apache Iceberg with AWS Glue

> It has been quite a ride getting Flink to standup with all of the other Apache dependencies.  Hopefully this stack spares others some suffering.

## Requirements

- Docker 
- Bash
- The [database-stack](https://github.com/seanhig/database-stack) is recommended.

## Setup

Make sure to copy the `.env-sample` file to a `.env` file and add the correct AWS credentials.

## Usage

```
docker compose build
docker compose up -d
```

This builds the base image with all depdencies, which include:

- CDC Connectors & JDBC Drivers for MySQL, Postgres, Oracle and SQL Server
- SerDe formats for JSON, Avro, Parquet and Iceberg (CSV and flat file are built into Flink)
- Hive Metastore dependencies for the local persistent Data Catalog

> The build will download hadoop and fetch a number of dependency jars into `/opt/flink/lib/stack` based on a Maven `pom.xml` file.  Additional `jars` can be added to the `maven pom.xml` and the container can be re-built, or they can be bundled into individual `jar-packs` and added at runtime (see below).

Once the stack is up and running you can shell into the `Flink-SQL client`:

```
./sql-client.sh
```
 
> Scaling the `taskmanager` can be done with `docker-compose scale taskmanager=3`.  Each taskmanager is configured to support 100 job slots.

See the [Streaming ETL to Iceberg](eg/streaming-etl-to-iceberg/) example to see what the `flink-stack` can do.

Happy flinking!

## Flink Catalogs

The folks at decodable put out a lot of good info that has helped with navigating the Apache docs, or lack therof.  This is a great primer [article](https://www.decodable.co/blog/catalogs-in-flink-sql-a-primer) on `Flink Catalogs`.

Essentially a catalog will persist your table definitions and source file locations (metadata) between sessions.  The `Flink SQL Client` by default will use an in-memory catalog that will disappear with each session.

`Flink Stack` supports two catalogs:

1. `Hive Metastore` for local definitons to source systems as it is supported out of the box with Flink.
2. `Glue` via Iceberg for the `Glue Data Catalog` integration.  Generally this catalog is only used for target Iceberg tables.

## Jar Usage and Build Notes

Flink is a great project, but an excellent illustration of the mess that has become Java Class loaders.  The entire mechanism when combined with dependency injection seems to completely overlook the complexity of managing the runtime environment.  Trying to get the various Apache projects to work together despite dependency conflicts is illustrative, and the nearly useless `ClassNotFoundException` or `java.lang.NoClassDefFoundError` exist only to waste your time.

Flink describes the [class loader hell](https://nightlies.apache.org/flink/flink-docs-master/docs/ops/debugging/debugging_classloading/) nicely.

In the end adding everything to `/opt/flink/lib/stack` worked best with the least class loader problems in `FLink-SQL` jobs.  This is the default.

Work is in progress to create a leaner base image and use bundled dependency jars into `Jar Packs` to be dynamically included, but this does not work in all cases. (eg. Adding a jar that contains sereral of the CDC connectors will not register all connectors, so those must be separated).

```
ADD JAR '/jar-packs/flink-stack-mysql.jar';
```

At the present it seems `HADOOP_CLASSPATH` needs to exist, at least in some form, and the easiest way to accomplish this is to bundle the latest hadoop version into the Flink image.  Bundling Hadoop jars without shading them as embedded (in a Java code usage model) simply does not work.

See the `eg` folder for examples, most are based on the `streaming-etl-to-iceberg` foundation.