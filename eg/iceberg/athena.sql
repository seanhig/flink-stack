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
