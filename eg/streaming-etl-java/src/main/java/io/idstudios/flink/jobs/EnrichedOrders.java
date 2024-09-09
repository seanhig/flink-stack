

package io.idstudios.flink.jobs;

import org.apache.flink.table.api.Table;
 //import org.apache.flink.table.api.Tumble;
 //import org.apache.flink.table.expressions.TimeIntervalUnit;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.*;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

 public class EnrichedOrders {
  
    private static final Logger LOG = LoggerFactory.getLogger(EnrichedOrders.class);

     public static void main(String[] args) throws Exception {

        Configuration config = new Configuration();
        config.set(PipelineOptions.NAME, "MySQL-enriched_orders-Java");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);
        
        env.enableCheckpointing(30000);
        
        tEnv.executeSql("CREATE TABLE products (\n" +
            "     id          INT,\n" +
            "     name        STRING,\n" + 
            "     description STRING,\n" +
            "     PRIMARY KEY (id) NOT ENFORCED\n" +
            ") WITH (\n" +
            "     'connector' = 'mysql-cdc',\n" + 
            "     'hostname' = 'host.docker.internal',\n" +
            "     'port' = '3306',\n" +
            "     'username' = 'root',\n" +
            "     'password' = 'Fender2000',\n" +
            "     'database-name' = 'erpdb',\n" +
            "     'table-name' = 'products'\n" +
            ")");
 

        tEnv.executeSql("CREATE TABLE orders (\n" +
            "      order_id          INT,\n" +
            "      order_date        TIMESTAMP(0),\n" +
            "      customer_name     STRING,\n" +
            "      price             DECIMAL(10, 5),\n" +
            "      product_id        INT,\n" + 
            "      order_status BOOLEAN,\n" + 
            " PRIMARY KEY (order_id) NOT ENFORCED\n" +
            ") WITH (\n" + 
            "     'connector' = 'mysql-cdc',\n" +
            "     'hostname' = 'host.docker.internal',\n" + 
            "     'port' = '3306',\n" + 
            "     'username' = 'root',\n" + 
            "     'password' = 'Fender2000',\n" + 
            "     'database-name' = 'erpdb',\n" + 
            "     'table-name' = 'orders'\n" + 
            ")");

        tEnv.executeSql("CREATE TABLE shipments (\n" + 
            "       shipment_id         INT,\n" + 
            "       order_id            INT,\n" + 
            "       origin              STRING,\n" + 
            "       destination         STRING,\n" + 
            "       is_arrived          BOOLEAN,\n" + 
            "PRIMARY KEY (shipment_id) NOT ENFORCED\n" + 
            ") WITH (\n" + 
            "       'connector' = 'postgres-cdc',\n" + 
            "       'hostname' = 'host.docker.internal',\n" + 
            "       'port' = '5432',\n" + 
            "       'username' = 'postgres',\n" + 
            "       'password' = 'Fender2000',\n" + 
            "       'database-name' = 'shipdb',\n" + 
            "       'schema-name' = 'public',\n" + 
            "       'table-name' = 'shipments',\n" + 
            "       'decoding.plugin.name' = 'pgoutput',\n" + 
            "       'slot.name' = 'flinkjava'\n" + 
            ")");

 
        tEnv.executeSql("CREATE TABLE enriched_orders (\n" + 
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
            "       'connector.url' = 'jdbc:mysql://host.docker.internal:3306/operations',\n" + 
            "       'connector.username' = 'root',\n" +
            "       'connector.password' = 'Fender2000',\n" + 
            "       'connector.table' = 'enriched_orders'\n" +
            ")");
 
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
 