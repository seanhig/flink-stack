SET execution.checkpointing.interval = 3s;

SET 'sql-client.verbose' = 'true';

ADD JAR '/jar-packs/flink-iceberg-uber.jar';

CREATE TABLE enriched_orders (
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


CREATE TABLE iceberg_enriched_orders_sink (
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
    'warehouse'='file:///data/iceberg/warehouse',
    'format-version'='2'
  );

SET 'pipeline.name' = 'Iceberg-enriched-orders';

INSERT INTO iceberg_enriched_orders_sink SELECT * FROM enriched_orders;





-- old crap
CREATE CATALOG iceberg_jdbc WITH (
   'type' = 'iceberg',
   'io-impl' = 'org.apache.iceberg.aws.s3.S3FileIO',
   'warehouse' = 's3://warehouse',
   's3.endpoint' = 'http://minio:9000',
   's3.path-style-access' = 'true',
   'catalog-impl' = 'org.apache.iceberg.jdbc.JdbcCatalog',
   'uri' ='jdbc:postgresql://host.docker.internal:5435/?user=dba&password=rules');

CREATE DATABASE `iceberg_jdbc`.orders;

USE `iceberg_jdbc`.orders;
CREATE TABLE t_foo (c1 varchar, c2 int);
INSERT INTO t_foo VALUES ('a',42);

SET 'execution.runtime-mode' = 'batch';
SET 'sql-client.execution.result-mode' = 'tableau';

-- Wait a few moments; running this straightaway often doesn't show
-- the results
SELECT * FROM t_foo;

SET execution.checkpointing.interval = 3s;
SET sql-client.execution.result-mode = 'tableau' ;

-- Flink SQL to define products and orders from erpdb
CREATE TABLE products (
    id INT,
    name STRING,
    description STRING,
    PRIMARY KEY (id) NOT ENFORCED
  ) WITH (
    'connector' = 'mysql-cdc',
    'hostname' = 'host.docker.internal',
    'port' = '3306',
    'username' = 'root',
    'password' = 'Fender2000',
    'database-name' = 'erpdb',
    'table-name' = 'products'
  );

CREATE TABLE orders (
   order_id INT,
   order_date TIMESTAMP(0),
   customer_name STRING,
   price DECIMAL(10, 5),
   product_id INT,
   order_status BOOLEAN,
   PRIMARY KEY (order_id) NOT ENFORCED
 ) WITH (
   'connector' = 'mysql-cdc',
   'hostname' = 'host.docker.internal',
   'port' = '3306',
   'username' = 'root',
   'password' = 'Fender2000',
   'database-name' = 'erpdb',
   'table-name' = 'orders'
 );

-- Flink SQL to define shipments from PostgreSQL shipdb
CREATE TABLE shipments (
   shipment_id INT,
   order_id INT,
   origin STRING,
   destination STRING,
   is_arrived BOOLEAN,
   PRIMARY KEY (shipment_id) NOT ENFORCED
 ) WITH (
   'connector' = 'postgres-cdc',
   'hostname' = 'host.docker.internal',
   'port' = '5432',
   'username' = 'postgres',
   'password' = 'Fender2000',
   'database-name' = 'shipdb',
   'schema-name' = 'public',
   'table-name' = 'shipments',
   'decoding.plugin.name' = 'pgoutput',
   'slot.name' = 'flink'
 );

CREATE TABLE iceberg_jdbc.orders.enriched_orders (
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
  ) WITH ('format-version'='2', 'write.upsert.enabled'='true');

SET 'pipeline.name' = 'Iceberg-enriched_orders';

INSERT INTO iceberg_jdbc.orders.enriched_orders
 SELECT o.*, p.name, p.description, s.shipment_id, s.origin, s.destination, s.is_arrived
 FROM orders AS o
 LEFT JOIN products AS p ON o.product_id = p.id
 LEFT JOIN shipments AS s ON o.order_id = s.order_id;

INSERT INTO iceberg_jdbc.orders.enriched_orders SELECT * FROM enriched_orders;


INSERT INTO enriched_orders
 SELECT o.*, p.name, p.description, s.shipment_id, s.origin, s.destination, s.is_arrived
 FROM orders AS o
 LEFT JOIN products AS p ON o.product_id = p.id
 LEFT JOIN shipments AS s ON o.order_id = s.order_id;



 -- Flink SQL to define the target enriched_orders table in the MySQL operations
CREATE TABLE enriched_orders (
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
    'connector.type' = 'jdbc',
    'connector.url' = 'jdbc:mysql://host.docker.internal:3306/operations',
    'connector.username' = 'root',
    'connector.password' = 'Fender2000',
    'connector.table' = 'enriched_orders'
  );



  --- AWS

  CREATE CATALOG iceberg_jdbc_aws WITH (
   'type' = 'iceberg',
   'io-impl' = 'org.apache.iceberg.aws.s3.S3FileIO',
   'warehouse' = 's3://ids-flink-demo-warehouse',
   's3.path-style-access' = 'true',
   'catalog-impl' = 'org.apache.iceberg.jdbc.JdbcCatalog',
   'uri' ='jdbc:postgresql://host.docker.internal:5435/?user=dba&password=rules');

CREATE DATABASE `iceberg_jdbc_aws`.orders;

SET execution.checkpointing.interval = 3s;
SET sql-client.execution.result-mode = 'tableau' ;

CREATE TABLE iceberg_jdbc_aws.orders.enriched_orders (
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
  ) WITH ('format-version'='2', 'write.upsert.enabled'='true');


SET 'pipeline.name' = 'Iceberg-enriched_orders';

INSERT INTO iceberg_jdbc_aws.orders.enriched_orders
 SELECT o.*, p.name, p.description, s.shipment_id, s.origin, s.destination, s.is_arrived
 FROM orders AS o
 LEFT JOIN products AS p ON o.product_id = p.id
 LEFT JOIN shipments AS s ON o.order_id = s.order_id;


--- Athena

CREATE TABLE enriched_orders (
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
   is_arrived BOOLEAN)
PARTITIONED BY (order_id)
LOCATION 's3://ids-flink-demo-warehouse/orders/enriched_orders' -- adjust to your S3 bucket name
TBLPROPERTIES ('table_type'='ICEBERG');
