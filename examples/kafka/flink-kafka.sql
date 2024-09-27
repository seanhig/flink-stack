SET execution.checkpointing.interval = 30s;

SET sql-client.execution.result-mode = 'tableau' ;

SET 'sql-client.verbose' = 'true';

CREATE TABLE weborders (
  web_order_id       STRING,
  order_date         TIMESTAMP(3),
  customer_name      STRING,
  destination        STRING,
  product_id         INT,
  quantity           INT
) WITH (
  'connector'                     = 'kafka',
  'topic'                         = 'weborders',
  'properties.bootstrap.servers'  = 'host.docker.internal:29092', 
  'properties.group.id'           = 'flink-weborder-processor',
  'value.format'                  = 'avro-confluent',
  'scan.startup.mode'             = 'earliest-offset',
  'value.avro-confluent.url'      = 'http://host.docker.internal:8081',
  'value.fields-include'          = 'EXCEPT_KEY'  
);

-- Always use port 29092 as this maps to external advertised listener of host.docker.internal

CREATE TABLE orders (
    order_id INT,
    order_ref STRING,
    order_date TIMESTAMP(3),
    customer_name STRING,
    order_total DECIMAL(10, 5),
    order_qty INTEGER,
    product_id INT,
    order_status INTEGER,
    PRIMARY KEY (order_id) NOT ENFORCED
)
WITH (
  'connector.type' = 'jdbc',
  'connector.url' = 'jdbc:mysql://host.docker.internal:3306/erpdb',
  'connector.username' = 'root',
  'connector.password' = 'Fender2000',
  'connector.table' = 'orders'
);

CREATE TABLE products (
    id INT,
    name STRING,
    description STRING,
    price DECIMAL(10, 5),
    PRIMARY KEY (id) NOT ENFORCED
)
WITH (
    'connector' = 'mysql-cdc',
    'hostname' = 'host.docker.internal',
    'port' = '3306',
    'username' = 'root',
    'password' = 'Fender2000',
    'database-name' = 'erpdb',
    'table-name' = 'products'
);

SET 'pipeline.name' = 'Kafka-weborders-processor';

INSERT INTO 
    orders
(
  order_ref,
  order_date,
  customer_name,
  order_total,
  order_qty,
  product_id
)
SELECT  wo.web_order_id, 
        wo.order_date, 
        wo.customer_name,
        wo.quantity * p.price, 
        wo.quantity,
        p.id
FROM
    weborders AS wo
    LEFT JOIN products AS p ON wo.product_id = p.id
WHERE p.price is NOT NULL;

SELECT  count(*)
FROM
    weborders AS wo
    LEFT JOIN products AS p ON wo.product_id = p.id
WHERE p.price is NOT NULL;

CREATE TABLE orders_cdc (
    order_id INT,
    order_ref STRING,
    order_date TIMESTAMP(0),
    customer_name STRING,
    order_total DECIMAL(10, 5),
    order_qty INTEGER,
    product_id INT,
    order_status INTEGER,
    PRIMARY KEY (order_id) NOT ENFORCED
)
WITH (
    'connector' = 'mysql-cdc',
    'hostname' = 'host.docker.internal',
    'port' = '3306',
    'username' = 'root',
    'password' = 'Fender2000',
    'database-name' = 'erpdb',
    'table-name' = 'orders'
);

CREATE TABLE shipments (
    shipment_id INT,
    order_id INT,
    origin STRING,
    destination STRING,
    has_arrived BOOLEAN,
    PRIMARY KEY (shipment_id) NOT ENFORCED
)
WITH (
  'connector.type' = 'jdbc',
  'connector.url' = 'jdbc:postgresql://host.docker.internal:5432/shipdb',
  'connector.username' = 'postgres',
  'connector.password' = 'Fender2000',
  'connector.table' = 'shipments'
);

SET 'pipeline.name' = 'Kafka-weborder-shipments';

INSERT INTO
    shipments
(
  order_id,
  destination
)
SELECT default,
    o.order_id, 
    wo.destination
FROM
    weborders AS wo
    LEFT JOIN products AS p ON wo.product_id = p.id
    LEFT JOIN order_cdc AS o ON wo.web_order_id = o.order_ref