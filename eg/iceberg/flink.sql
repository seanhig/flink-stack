SET execution.checkpointing.interval = 3s;

SET 'sql-client.verbose' = 'true';

ADD JAR '/jar-packs/flink-stack-mysql.jar';

-- AWS Iceberg CDC

CREATE TABLE enriched_orders_cdc (
   order_id INT,
   order_date TIMESTAMP(3),
   customer_name STRING,
   price DECIMAL(10, 5),
   product_id INT,
   order_status BOOLEAN,
   product_name STRING,
   product_description STRING,
   shipment_id INT,
   origin STRING,
   destination STRING,
   is_arrived BOOLEAN,
   PRIMARY KEY (order_id) NOT ENFORCED ) 
   WITH (
   'connector' = 'mysql-cdc',
   'hostname' = 'host.docker.internal',
   'port' = '3306',
   'username' = 'root',
   'password' = 'Fender2000',
   'database-name' = 'operations',
   'table-name' = 'enriched_orders'
 );

CREATE TABLE iceberg_enriched_orders_sink_aws (
   order_id INT,
   order_date TIMESTAMP(3),
   customer_name STRING,
   price DECIMAL(10, 5),
   product_id INT,
   order_status BOOLEAN,
   product_name STRING,
   product_description STRING,
   shipment_id INT,
   origin STRING,
   destination STRING,
   is_arrived BOOLEAN,
   PRIMARY KEY (order_id) NOT ENFORCED ) 
   WITH (
    'connector'='iceberg',
    'catalog-name'='iceberg_catalog',
    'catalog-type'='hadoop',  
    'warehouse'='s3a://ids-flink-demo-warehouse/iceberg_enriched_orders',
    'format-version'='2'
  );

SET 'pipeline.name' = 'Iceberg-enriched-orders-aws';

INSERT INTO iceberg_enriched_orders_sink_aws SELECT * FROM enriched_orders_cdc;

-- Works ok initially but will fail on an update
-- 2024-04-05 11:39:31
-- java.lang.ArrayIndexOutOfBoundsException: Index 2 out of bounds for length 1
-- 	at org.apache.flink.table.data.binary.BinarySegmentUtils.getLongMultiSegments(BinarySegmentUtils.java:736)
-- 	at org.apache.flink.table.data.binary.BinarySegmentUtils.getLong(BinarySegmentUtils.java:726)
-- 	at org.apache.flink.table.data.binary.BinarySegmentUtils.readTimestampData(BinarySegmentUtils.java:1022)
-- 	at org.apache.flink.table.data.binary.BinaryRowData.getTimestamp(BinaryRowData.java:356)
-- 	at org.apache.flink.table.data.RowData.lambda$createFieldGetter$39385f9c$1(RowData.java:260)
-- 	at org.apache.flink.table.data.RowData.lambda$createFieldGetter$25774257$1(RowData.java:296)
-- 	at org.apache.iceberg.flink.data.FlinkParquetWriters$RowDataWriter.get(FlinkParquetWriters.java:501)
-- 	at org.apache.iceberg.flink.data.FlinkParquetWriters$RowDataWriter.get(FlinkParquetWriters.java:488)

-- Local using CDC (same error)

CREATE TABLE iceberg_enriched_orders_sink (
   order_date TIMESTAMP(3),
   order_id INT,
   customer_name STRING,
   price DECIMAL(10, 5),
   product_id INT,
   order_status BOOLEAN,
   product_name STRING,
   product_description STRING,
   shipment_id INT,
   origin STRING,
   destination STRING,
   is_arrived BOOLEAN,
   PRIMARY KEY (order_id) NOT ENFORCED ) 
   WITH (
    'connector'='iceberg',
    'catalog-name'='iceberg_catalog',
    'catalog-type'='hadoop',  
    'warehouse'='file:///data/iceberg/warehouse',
    'format-version'='2'
  );

SET 'pipeline.name' = 'Iceberg-enriched-orders';

INSERT INTO iceberg_enriched_orders_sink SELECT * FROM enriched_orders_cdc;

-- One time Dump using enriched_orders JDBC from streaming-etl

CREATE TABLE iceberg_enriched_orders_dump_aws (
   order_id INT,
   order_date TIMESTAMP(3),
   customer_name STRING,
   price DECIMAL(10, 5),
   product_id INT,
   order_status BOOLEAN,
   product_name STRING,
   product_description STRING,
   shipment_id INT,
   origin STRING,
   destination STRING,
   is_arrived BOOLEAN,
   PRIMARY KEY (order_id) NOT ENFORCED ) 
   WITH (
    'connector'='iceberg',
    'catalog-name'='iceberg_catalog',
    'catalog-type'='hadoop',  
    'warehouse'='s3a://ids-flink-demo-warehouse/iceberg_enriched_orders_dump',
    'format-version'='2'
  );

SET 'pipeline.name' = 'Iceberg-enriched-orders-dump-aws';

INSERT INTO iceberg_enriched_orders_dump_aws SELECT * FROM enriched_orders;
