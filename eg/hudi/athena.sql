CREATE EXTERNAL TABLE `hudi_orders`(
  `_hoodie_commit_time` string, 
  `_hoodie_commit_seqno` string, 
  `_hoodie_record_key` string, 
  `_hoodie_partition_path` string, 
  `_hoodie_file_name` string, 
  `order_id` int,
  `order_date` timestamp,
  `customer_name` string,
  `price` decimal(10, 5),
  `product_id` int,
  `order_status` boolean,
  `product_name` string,
  `product_description` string,
  `shipment_id` int,
  `origin` string,
  `destination` string,
  `is_arrived` boolean)
PARTITIONED BY ( 
  `order_id` int)
ROW FORMAT SERDE 
  'org.apache.hadoop.hive.ql.io.parquet.serde.ParquetHiveSerDe' 
STORED AS INPUTFORMAT 
  'org.apache.hudi.hadoop.HoodieParquetInputFormat' 
OUTPUTFORMAT 
  'org.apache.hadoop.hive.ql.io.parquet.MapredParquetOutputFormat' 
LOCATION
  's3://ids-flink-demo-warehouse/enriched_orders'