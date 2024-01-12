# Flink Stack

A `docker-compose` stack for Flink and Flink-SQL development.

Note bind mount mappings to local folders.  All `*.jar` files located in `/host/jars` will be copied to `/opt/flink/lib`.

`bash launch-stack.sh` will launch the stack.

`docker run sql-client` will bring up the Flink-SQL client.

Happy flinking.