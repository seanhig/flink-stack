SET execution.checkpointing.interval = 30s;
SET 'sql-client.verbose' = 'true';

-- ADD JAR '/jar-packs/flink-stack-mysql.jar';
-- ADD JAR '/jar-packs/flink-stack-iceberg.jar';

-- Iceberg CDC Source table

CREATE TABLE enriched_orders_cdc (
    order_id INT,
    order_date TIMESTAMP(3),
    customer_name STRING,
    order_total DECIMAL(10, 5) ,
    order_qty INTEGER,
    product_id INTEGER,
    order_status INTEGER, 
    product_name STRING,
    product_description STRING,
    product_price DECIMAL(10, 5),
    shipment_id INTEGER,
    origin STRING,
    destination STRING,
    has_arrived BOOLEAN
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

USE CATALOG iceberg_catalog;

CREATE DATABASE iceberg_orders;

-- Verify in AWS Console that the database exists in the Glue Catalog

USE iceberg_orders;

CREATE TABLE enriched_orders_lake (
    order_id INT,
    order_date TIMESTAMP(3),
    customer_name STRING,
    order_total DECIMAL(10, 5) ,
    product_id INTEGER,
    shipment_id INTEGER,
    origin STRING,
    order_status INTEGER, 
    product_name STRING,
    product_price DECIMAL(10, 5),
    order_qty INTEGER,
    product_description STRING,
    destination STRING,
    has_arrived BOOLEAN
   PRIMARY KEY (order_id) NOT ENFORCED ) ;
   
SET 'pipeline.name' = 'Iceberg-enriched-orders-aws';

-- This will create the streaming CDC to Iceberg job
INSERT INTO iceberg_catalog.iceberg_orders.enriched_orders_lake SELECT * FROM default_catalog.default_database.enriched_orders_cdc;
