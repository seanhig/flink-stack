SET execution.checkpointing.interval = 3s;

SET 'sql-client.verbose' = 'true';

ADD JAR '/jar-packs/flink-stack-mysql.jar';

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
