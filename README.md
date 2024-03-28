# Flink Stack

A `docker-compose` stack for Flink and Flink-SQL development.

> This uses a custom-built uber Flink docker image that must be pre-built in the image folder.  As this is primarily for `Flink-SQL` work all `jars` are added to the container in advance.

## Build Image

```
cd image
./fetch-hadoop.sh
./fetch-jars.sh
./build.sh
```

This will download hadoop and fetch a number of dependency jars.  Additional `jars` can be added to the `maven pom.xml` file used by the `fetch-jars.sh` script.
 
## Launch Flink Stack

`docker-compose up -d` will launch the stack.

`docker run sql-client` will bring up the Flink-SQL client.

> Scaling the `taskmanager` can be done with `docker-compose scale taskmanager=3`.  Each taskmanager is configured to support 100 job slots.

See the [Streaming ETL](streaming-etl/) example for fun stuff to do with the `flink-stack`.

Happy flinking.

