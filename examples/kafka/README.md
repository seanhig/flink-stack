# Kafka Web Orders Processing with Flink, Spring Boot and Spring JPA / Kafka
This example explores Flink development with Kafka.

The `webordergen` is a Spring Boot Kafka application that generates fake orders at a pre-defined pace and places them on an [Apache Kafka]() topic to simulate an online web application order system.

The `weborder-processor`is a Spring Boot Kafka and JPA application that consumes messages from the `weborders` Kafka topic and writes them to the `MySQL Orders` and `Postgres Shipments` tables respectively.

The `weborders` generated to the Kafka topic can then be used for analysis in [Apache Flink](), and serve as real world data input simulation for exploration of Flink CDC data movement and analytic processing.

> Initial plan was to use `Flink SQL` to intake directly from Kafka into the `MySQL` and `Postgres` tables, but this revealed a current limitation in Flink.  Specifically, it is presently not possible to stream into a table with a database based IDENTITY column.  
