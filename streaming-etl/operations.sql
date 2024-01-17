-- MySQL
CREATE DATABASE operational_datastore;
USE operational_datastore;

CREATE TABLE enriched_orders (
  order_id INTEGER NOT NULL PRIMARY KEY,
  order_date TIMESTAMP NOT NULL,
  customer_name VARCHAR(255) ,
  price DECIMAL(10, 5) ,
  product_id INTEGER NOT NULL,
  shipment_id INTEGER,
  origin VARCHAR(255),
  order_status BOOLEAN, -- Whether order has been placed
  product_name VARCHAR(255),
  product_description VARCHAR(512),
  destination VARCHAR(255),
  is_arrived BOOLEAN
);
