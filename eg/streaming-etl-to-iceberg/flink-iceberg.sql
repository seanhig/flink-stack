SET execution.checkpointing.interval = 600s;
SET 'sql-client.verbose' = 'true';

-- Iceberg CDC Source table

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

-- Catalog aligns with default Aws Catalog in account
CREATE CATALOG iceberg_catalog WITH (
  'type'='iceberg',
  'warehouse'='s3a://ids-flink-demo-warehouse',
  'catalog-impl'='org.apache.iceberg.aws.glue.GlueCatalog',
  'io-impl'='org.apache.iceberg.aws.s3.S3FileIO'
);

CREATE DATABASE iceberg_orders;

-- Verify in AWS Console that the database exists in the Glue Catalog

USE iceberg_orders;

CREATE TABLE enriched_orders_lake (
   order_id INT,
   order_date TIMESTAMP,
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
   PRIMARY KEY (order_id) NOT ENFORCED ) ;
   
SET 'pipeline.name' = 'Iceberg-enriched-orders-aws';

-- This will create the streaming CDC to Iceberg job
INSERT INTO iceberg_catalog.iceberg_orders.enriched_orders_lake SELECT * FROM default_catalog.default_database.enriched_orders_cdc;


