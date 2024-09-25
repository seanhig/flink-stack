-- MySQL source
SET GLOBAL binlog_format = 'ROW';
CREATE DATABASE erpdb;
USE erpdb;
CREATE TABLE products (
  id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512),
  price  DECIMAL(10, 5) NOT NULL
) AUTO_INCREMENT = 101;

INSERT INTO products
VALUES (default,"scooter","Small 2-wheel scooter", 107.00),
       (default,"car battery","12V car battery", 85.99),
       (default,"12-pack drill bits","12-pack of drill bits with sizes ranging from #40 to #3", 35.99),
       (default,"hammer","12oz carpenter's hammer", 12.50),
       (default,"hammer","14oz carpenter's hammer", 13.50),
       (default,"hammer","16oz carpenter's hammer", 15.25),
       (default,"rocks","box of assorted rocks", 4.99),
       (default,"jacket","water resistent black wind breaker", 24.99),
       (default,"spare tire","24 inch spare tire", 115.0);

CREATE TABLE orders (
  order_id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
  order_date DATETIME NOT NULL,
  customer_name VARCHAR(255) NOT NULL,
  order_total DECIMAL(10, 5) NOT NULL,
  qty INTEGER NOT NULL,
  product_id INTEGER NOT NULL,
  order_status INTEGER NOT NULL 
) AUTO_INCREMENT = 10001;

INSERT INTO orders
VALUES (default, '2020-07-30 10:08:22', 'Jark', 85.99, 1, 102, 0),
       (default, '2020-07-30 10:11:09', 'Sally', 13.50, 1, 105, 0),
       (default, '2020-07-30 12:00:30', 'Edward', 15.25, 1, 106, 0);