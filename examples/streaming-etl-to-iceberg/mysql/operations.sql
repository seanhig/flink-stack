-- MySQL target 
CREATE DATABASE operations;
USE operations;

CREATE TABLE enriched_orders (
  order_id INTEGER NOT NULL PRIMARY KEY,
  order_date TIMESTAMP NOT NULL,
  customer_name VARCHAR(255) ,
  order_total DECIMAL(10, 5) ,
  product_id INTEGER NOT NULL,
  shipment_id INTEGER,
  origin VARCHAR(255),
  order_status INTEGER, 
  product_name VARCHAR(255),
  product_price DECIMAL(10, 5),
  order_qty INTEGER,
  product_description VARCHAR(512),
  destination VARCHAR(255),
  has_arrived BOOLEAN
);
