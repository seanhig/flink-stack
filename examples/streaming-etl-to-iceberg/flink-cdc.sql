-- Set options
SET execution.checkpointing.interval = 30s;

SET sql-client.execution.result-mode = 'tableau' ;

SET 'sql-client.verbose' = 'true';

-- only required when running in unbundled flink instance
-- for ease of use, all common dependencies are built into the
-- flink docker instance
-- experience suggests this approach is prone to class loading
-- issues
-- it is better to build a custom docker image and deploy to K8s
--ADD JAR '/jar-packs/flink-stack-mysql.jar';
--ADD JAR '/jar-packs/flink-stack-postgres.jar';
--ADD JAR '/jar-packs/flink-stack-jdbc.jar';

-- only when persisting the catalog in the hive
-- metastore, if skipped will use inmem default catalog
--
-- CREATE CATALOG hive_catalog WITH (
--     'type' = 'hive',
--     'hive-conf-dir' = '/opt/flink/conf'
-- );
--
-- set the Hive Catalog as the current catalog of the session
--USE CATALOG hive_catalog;

-- Flink SQL to define products and orders from erpdb
CREATE TABLE products (
    id INT,
    name STRING,
    description STRING,
    product_price DECIMAL(10, 5),
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

CREATE TABLE orders (
    order_id INT,
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

-- Flink SQL to define shipments from PostgreSQL shipdb
CREATE TABLE shipments (
    shipment_id INT,
    order_id INT,
    origin STRING,
    destination STRING,
    has_arrived BOOLEAN,
    PRIMARY KEY (shipment_id) NOT ENFORCED
)
WITH (
    'connector' = 'postgres-cdc',
    'hostname' = 'host.docker.internal',
    'port' = '5432',
    'username' = 'postgres',
    'password' = 'Fender2000',
    'database-name' = 'shipdb',
    'schema-name' = 'public',
    'table-name' = 'shipments',
    'decoding.plugin.name' = 'pgoutput',
    'slot.name' = 'flink'
);

-- Flink SQL to define the target enriched_orders table in the MySQL operations
CREATE TABLE enriched_orders (
    order_id INT,
    order_date TIMESTAMP(3),
    customer_name STRING,
    order_total DECIMAL(10, 5) ,
    order_qty INTEGER,
    product_id INTEGER,
    order_status INTEGER, 
    product_name STRING,
    product_description STRING,
    product_price DECIMAL(10, 5),
    shipment_id INTEGER,
    origin STRING,
    destination STRING,
    has_arrived BOOLEAN
    PRIMARY KEY (order_id) NOT ENFORCED
)
WITH (
    'connector.type' = 'jdbc',
    'connector.url' = 'jdbc:mysql://host.docker.internal:3306/operations',
    'connector.username' = 'root',
    'connector.password' = 'Fender2000',
    'connector.table' = 'enriched_orders'
);

-- select * from enriched_orders;

-- Sets the job name for the any SQL that follows
SET 'pipeline.name' = 'MySQL-enriched_orders';

-- Creates a streaming ETL job to provide real-time updates to the enriched_orders table
INSERT INTO
    enriched_orders
SELECT o.*, p.name, p.description, p.price, s.shipment_id, s.origin, s.destination, s.is_arrived
FROM
    orders AS o
    LEFT JOIN products AS p ON o.product_id = p.id
    LEFT JOIN shipments AS s ON o.order_id = s.order_id;