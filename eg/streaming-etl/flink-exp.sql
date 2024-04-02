DROP TABLE enriched_orders_dl;
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
    'connector' = 'filesystem',
    'path' = 'file:///data/',
    'format' = 'parquet'
  );

-- Sets the job name for the any SQL that follows
SET 'pipeline.name' = 'Parquet-orders-dl';

-- Creates a one time batch ETL job to provide a parquet dump of enriched_orders_dl table
INSERT INTO enriched_orders_dl SELECT * FROM enriched_orders;