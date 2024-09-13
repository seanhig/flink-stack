

package io.idstudios.flink.jobs;

import org.apache.flink.table.api.Table;
 //import org.apache.flink.table.api.Tumble;
 //import org.apache.flink.table.expressions.TimeIntervalUnit;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.*;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class EnrichedOrders extends FlinkJob {

    private static final Logger LOG = LoggerFactory.getLogger(EnrichedOrders.class);
    private static final String DEFAULT_CONFIG_PATH = "/flink-jobjars/enriched-orders-job.properties";
  
    public static void main(String[] args) throws Exception {

        loadJobConfig(EnrichedOrders.DEFAULT_CONFIG_PATH, args);

        Configuration config = new Configuration();
        config.set(PipelineOptions.NAME, "MySQL-enriched_orders-Java");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        
        env.enableCheckpointing(30000);

        String productsTemplate = "CREATE TABLE products (\n" +
            "     id          INT,\n" +
            "     name        STRING,\n" + 
            "     description STRING,\n" +
            "     PRIMARY KEY (id) NOT ENFORCED\n" +
            ") WITH (\n" +
            "     'connector' = 'mysql-cdc',\n" + 
            "     'hostname' = '%s',\n" +
            "     'port' = '%s',\n" +
            "     'username' = '%s',\n" +
            "     'password' = '%s',\n" +
            "     'database-name' = '%s',\n" +
            "     'table-name' = 'products'\n" +
            ")";
                
        String productSql = String.format(productsTemplate,
            jobConfig.get("mysql.erpdb.host.name"),
            jobConfig.get("mysql.erpdb.db.port"),
            jobConfig.get("mysql.erpdb.db.username"),
            jobConfig.get("mysql.erpdb.db.password"),
            jobConfig.get("mysql.erpdb.db.name"));

        String ordersTemplate = "CREATE TABLE orders (\n" +
            "      order_id          INT,\n" +
            "      order_date        TIMESTAMP(0),\n" +
            "      customer_name     STRING,\n" +
            "      price             DECIMAL(10, 5),\n" +
            "      product_id        INT,\n" + 
            "      order_status BOOLEAN,\n" + 
            " PRIMARY KEY (order_id) NOT ENFORCED\n" +
            ") WITH (\n" + 
            "     'connector' = 'mysql-cdc',\n" +
            "     'hostname' = '%s',\n" + 
            "     'port' = '%s',\n" + 
            "     'username' = '%s',\n" + 
            "     'password' = '%s',\n" + 
            "     'database-name' = '%s',\n" + 
            "     'table-name' = 'orders'\n" + 
            ")";

        String orderSql = String.format(ordersTemplate,
            jobConfig.get("mysql.erpdb.host.name"),
            jobConfig.get("mysql.erpdb.db.port"),
            jobConfig.get("mysql.erpdb.db.username"),
            jobConfig.get("mysql.erpdb.db.password"),
            jobConfig.get("mysql.erpdb.db.name"));

        String shipTemplate = "CREATE TABLE shipments (\n" + 
            "       shipment_id         INT,\n" + 
            "       order_id            INT,\n" + 
            "       origin              STRING,\n" + 
            "       destination         STRING,\n" + 
            "       is_arrived          BOOLEAN,\n" + 
            "PRIMARY KEY (shipment_id) NOT ENFORCED\n" + 
            ") WITH (\n" + 
            "       'connector' = 'postgres-cdc',\n" + 
            "       'hostname' = '%s',\n" + 
            "       'port' = '%s',\n" + 
            "       'username' = '%s',\n" + 
            "       'password' = '%s',\n" + 
            "       'database-name' = '%s',\n" + 
            "       'schema-name' = '%s',\n" + 
            "       'table-name' = 'shipments',\n" + 
            "       'decoding.plugin.name' = 'pgoutput',\n" + 
            "       'slot.name' = '%s'\n" + 
            ")";
        
        String shipSql = String.format(shipTemplate,
            jobConfig.get("postgres.shipdb.host.name"),
            jobConfig.get("postgres.shipdb.db.port"),
            jobConfig.get("postgres.shipdb.db.username"),
            jobConfig.get("postgres.shipdb.db.password"),
            jobConfig.get("postgres.shipdb.db.name"),
            jobConfig.get("postgres.shipdb.schema.name"),
            jobConfig.get("postgres.shipdb.db.slotname"));

        String enrichedTemplate = "CREATE TABLE enriched_orders (\n" + 
            "       order_id                INT,\n" + 
            "       order_date              TIMESTAMP(3),\n" + 
            "       customer_name           STRING,\n" + 
            "       price                   DECIMAL(10, 5),\n" + 
            "       product_id              INT,\n" + 
            "       order_status            BOOLEAN,\n" + 
            "       product_name            STRING,\n" + 
            "       product_description     STRING,\n" + 
            "       shipment_id             INT,\n" + 
            "       origin                  STRING,\n" + 
            "       destination             STRING,\n" + 
            "       is_arrived              BOOLEAN,\n" + 
            "       PRIMARY KEY (order_id) NOT ENFORCED\n" + 
            ") WITH (\n" + 
            "       'connector.type' = 'jdbc',\n" + 
            "       'connector.url' = 'jdbc:mysql://%s:%s/%s',\n" + 
            "       'connector.username' = '%s',\n" +
            "       'connector.password' = '%s',\n" + 
            "       'connector.table' = 'enriched_orders'\n" +
            ")";

        String enrichedOrdersSql = String.format(enrichedTemplate,
            jobConfig.get("mysql.opsdb.host.name"),
            jobConfig.get("mysql.opsdb.db.port"),
            jobConfig.get("mysql.opsdb.db.username"),
            jobConfig.get("mysql.opsdb.db.password"),
            jobConfig.get("mysql.opsdb.db.name"));
        
    
        tEnv.executeSql(productSql);
        tEnv.executeSql(orderSql);
        tEnv.executeSql(shipSql);
        tEnv.executeSql(enrichedOrdersSql);
             
        Table products = tEnv.from("products").select(
            $("id").as("product_id"), 
            $("name").as("product_name"), 
            $("description").as("product_description")
        );

        Table shipments = tEnv.from("shipments").select(
            $("shipment_id"), 
            $("order_id").as("shipment_order_id"), 
            $("origin"), 
            $("destination"), 
            $("is_arrived")
        );

        Table orders = tEnv.from("orders").select(
            $("order_id"), 
            $("order_date"), 
            $("customer_name"), 
            $("price"), 
            $("product_id").as("order_product_id"),
            $("order_status")
        );

        LOG.info("Running the CDC UPSERT!"); 

        Table results = orders.join(products).join(shipments)
            .where($("order_product_id").isEqual($("product_id")))
            .where($("order_id").isEqual($("shipment_order_id")))
            .select($("order_id"),
                $("order_date"),
                $("customer_name"),
                $("price"),
                $("product_id"),
                $("order_status"),
                $("product_name"),
                $("product_description"),
                $("shipment_id"),
                $("origin"),
                $("destination"),
                $("is_arrived")
            );

        results.executeInsert("enriched_orders"); 
     }
 }
 