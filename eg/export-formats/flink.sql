-- use enriched_orders from streaming-etl

SET 'sql-client.verbose' = 'true';

-- AVRO

ADD JAR '/jar-packs/flink-stack-avro.jar';

CREATE TABLE enriched_orders_avro (
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
   PRIMARY KEY (order_id) NOT ENFORCED
  ) WITH (
    'connector' = 'filesystem',
    'path' = 'file:///data/',
    'format' = 'avro'
  );

-- Sets the job name for the any SQL that follows
SET 'pipeline.name' = 'Avro-orders-export';

-- Creates a one time batch ETL job to provide a parquet dump of enriched_orders_dl table
INSERT INTO enriched_orders_avro SELECT * FROM enriched_orders;

-- DROP TABLE enriched_orders_avro;

-- Parquet

ADD JAR '/jar-packs/flink-stack-parquet.jar';

CREATE TABLE enriched_orders_parquet (
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
   PRIMARY KEY (order_id) NOT ENFORCED
  ) WITH (
    'connector' = 'filesystem',
    'path' = 'file:///data/',
    'format' = 'parquet'
  );

-- Sets the job name for the any SQL that follows
SET 'pipeline.name' = 'Parquet-orders-export';

INSERT INTO enriched_orders_parquet SELECT * FROM enriched_orders;

-- DROP TABLE enriched_orders_parquet;


-- Parquet

ADD JAR '/jar-packs/flink-stack-json.jar';
CREATE TABLE enriched_orders_json (
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
   PRIMARY KEY (order_id) NOT ENFORCED
  ) WITH (
    'connector' = 'filesystem',
    'path' = 'file:///data/',
    'format' = 'json'
  );

-- Sets the job name for the any SQL that follows
SET 'pipeline.name' = 'JSON-orders-export';

INSERT INTO enriched_orders_json SELECT * FROM enriched_orders;

-- DROP TABLE enriched_orders_json;

-- CSV
CREATE TABLE enriched_orders_csv (
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
   PRIMARY KEY (order_id) NOT ENFORCED
  ) WITH (
    'connector' = 'filesystem',
    'path' = 'file:///data/',
    'format' = 'csv'
  );

-- Sets the job name for the any SQL that follows
SET 'pipeline.name' = 'CSV-orders-export';

INSERT INTO enriched_orders_csv SELECT * FROM enriched_orders;

-- DROP TABLE enriched_orders_csv;
