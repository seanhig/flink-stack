
SELECT * from enriched_orders_lake 


-- Time travel query
SELECT * from enriched_orders_lake FOR TIMESTAMP AS OF (current_timestamp - interval '2' minute) WHERE order_id = 10001 


SELECT * FROM "iceberg_orders"."enriched_orders_lake$history" 