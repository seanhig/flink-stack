package io.idstudios.flink.jobs.enrichedorders;

import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.idstudios.flink.jobs.FlinkJob;
import io.idstudios.flink.jobs.JobConfig;

import static org.apache.flink.table.api.Expressions.*;

public class EnrichedOrdersMySQL extends FlinkJob {

    private static final Logger log = LoggerFactory.getLogger(EnrichedOrdersMySQL.class);
    private static final String DEFAULT_CONFIG_PATH = "/flink-job-configs/enriched-orders-jobs.properties";

    public static void main(String[] args) throws Exception {

        JobConfig jobConfig = loadJobConfig(EnrichedOrdersMySQL.DEFAULT_CONFIG_PATH, args);

        configureEnrichedOrdersJob(jobConfig);
    }

    private static void configureEnrichedOrdersJob(JobConfig jobConfig) throws Exception {

        Configuration config = new Configuration();
        config.set(PipelineOptions.NAME, "MySQL-enriched_orders-Java");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);

        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        env.enableCheckpointing(30000);

        String productsTemplate = "CREATE TABLE products (\n" +
                "     id          INT,\n" +
                "     name        STRING,\n" +
                "     description STRING,\n" +
                "     price       DECIMAL(10, 5),\n" +
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
                "      order_total       DECIMAL(10, 5),\n" +
                "      order_qty         INT,\n" +
                "      product_id        INT,\n" +
                "      order_status      INT,\n" +
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
                "       has_arrived         BOOLEAN,\n" +
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
                "       order_total             DECIMAL(10, 5),\n" +
                "       order_qty               INT,\n" +
                "       product_id              INT,\n" +
                "       order_status            INT,\n" +
                "       product_name            STRING,\n" +
                "       product_description     STRING,\n" +
                "       product_price           DECIMAL(10, 5),\n" +
                "       shipment_id             INT,\n" +
                "       origin                  STRING,\n" +
                "       destination             STRING,\n" +
                "       has_arrived             BOOLEAN,\n" +
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
                jobConfig.get("mysql.opsdb.db.name"),
                jobConfig.get("mysql.opsdb.db.username"),
                jobConfig.get("mysql.opsdb.db.password"));

        tEnv.executeSql(productSql);
        tEnv.executeSql(orderSql);
        tEnv.executeSql(shipSql);
        tEnv.executeSql(enrichedOrdersSql);

        Table products = tEnv.from("products").select(
                $("id").as("product_id"),
                $("name").as("product_name"),
                $("price").as("product_price"),
                $("description").as("product_description"));

        Table shipments = tEnv.from("shipments").select(
                $("shipment_id"),
                $("order_id").as("shipment_order_id"),
                $("origin"),
                $("destination"),
                $("has_arrived"));

        Table orders = tEnv.from("orders").select(
                $("order_id"),
                $("order_date"),
                $("customer_name"),
                $("order_total"),
                $("order_qty"),
                $("product_id").as("order_product_id"),
                $("order_status"));

        log.info("Running the CDC UPSERT!");

        Table results = orders.join(products).join(shipments)
                .where($("order_product_id").isEqual($("product_id")))
                .where($("order_id").isEqual($("shipment_order_id")))
                .select($("order_id"),
                        $("order_date"),
                        $("customer_name"),
                        $("order_total"),
                        $("order_qty"),
                        $("product_id"),
                        $("order_status"),
                        $("product_name"),
                        $("product_description"),
                        $("product_price"),
                        $("shipment_id"),
                        $("origin"),
                        $("destination"),
                        $("has_arrived"));

        results.executeInsert("enriched_orders");
    }

}
