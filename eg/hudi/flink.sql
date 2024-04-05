
SET 'sql-client.verbose' = 'true';

ADD JAR '/jar-packs/flink-hudi-uber.jar';

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
   PRIMARY KEY (order_id) NOT ENFORCED ) WITH (
   'connector' = 'mysql-cdc',
   'hostname' = 'host.docker.internal',
   'port' = '3306',
   'username' = 'root',
   'password' = 'Fender2000',
   'database-name' = 'operations',
   'table-name' = 'enriched_orders'
 );

CREATE TABLE enriched_orders_dl (
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

CREATE TABLE enriched_orders_hudi_sink (
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
 )
 PARTITIONED BY (`order_id`)
 WITH (
    'connector' = 'hudi',
    'path' = 's3a://ids-flink-demo-warehouse/enriched_orders' ,
    'table.type' = 'COPY_ON_WRITE',
    'cdc.enabled' = 'true' ,
    'read.streaming.enabled' = 'true',  -- this option enable the streaming read
    'read.streaming.check-interval' = '4'     
 );

SET 'execution.runtime-mode' = 'streaming';
SET 'pipeline.name' = 'Hudi-enriched-orders';

INSERT INTO enriched_orders_hudi_sink SELECT * FROM enriched_orders;

INSERT INTO enriched_orders_hudi_sink SELECT * FROM enriched_orders_dl;

SET 'execution.runtime-mode' = 'batch';

---

CREATE TABLE enriched_orders_hudi_sink (
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
 )
 PARTITIONED BY (`order_id`)
 WITH (
    'connector' = 'hudi',
    'path' = 'file:///data/orders/' ,
    'table.type' = 'MERGE_ON_READ' ,
    'cdc.enabled' = 'true' ,
    'read.streaming.enabled' = 'true',  -- this option enable the streaming read
    'read.streaming.check-interval' = '4'     

 );

CREATE TABLE enriched_orders_hudi_sink (
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
 )
 PARTITIONED BY (`order_id`)
 WITH (
    'connector' = 'hudi',
    'path' = 'file:///data/orders/' ,
    'table.type' = 'COPY_ON_WRITE',
    'cdc.enabled' = 'true' 
 );

CREATE TABLE hudi_table(
    ts BIGINT,
    uuid VARCHAR(40) PRIMARY KEY NOT ENFORCED,
    rider VARCHAR(20),
    driver VARCHAR(20),
    fare DOUBLE,
    city VARCHAR(20)
)
PARTITIONED BY (`city`)
WITH (
  'connector' = 'hudi',
  'path' = 'file:///data/hudi_table',
  'table.type' = 'MERGE_ON_READ'
);

-- insert data using values
INSERT INTO hudi_table
VALUES
(1695159649087,'334e26e9-8355-45cc-97c6-c31daf0df330','rider-A','driver-K',19.10,'san_francisco'),
(1695091554788,'e96c4396-3fad-413a-a942-4cb36106d721','rider-C','driver-M',27.70 ,'san_francisco'),
(1695046462179,'9909a8b1-2d15-4d3d-8ec9-efc48c536a00','rider-D','driver-L',33.90 ,'san_francisco'),
(1695332066204,'1dced545-862b-4ceb-8b43-d2a568f6616b','rider-E','driver-O',93.50,'san_francisco'),
(1695516137016,'e3cf430c-889d-4015-bc98-59bdce1e530c','rider-F','driver-P',34.15,'sao_paulo'),
(1695376420876,'7a84095f-737f-40bc-b62f-6b69664712d2','rider-G','driver-Q',43.40 ,'sao_paulo'),
(1695173887231,'3eeb61f7-c2b0-4636-99bd-5d7a5a1d2c04','rider-I','driver-S',41.06 ,'chennai'),
(1695115999911,'c8abbe79-8d89-47ea-b4ce-4d224bae5bfa','rider-J','driver-T',17.85,'chennai');

select * from hudi_table;