package io.idstudios.flink.jobs;

import java.util.Arrays;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnrichedOrdersIceberg extends FlinkJob {

  private static final Logger LOG = LoggerFactory.getLogger(EnrichedOrdersIceberg.class);
  private static final String DEFAULT_CONFIG_PATH = "/flink-jobjars/enriched-orders-job.properties";

  public static void main(String[] args) throws Exception {

    JobConfig jobConfig = loadJobConfig(EnrichedOrdersIceberg.DEFAULT_CONFIG_PATH, args);

    Configuration config = new Configuration();
    config.set(PipelineOptions.NAME, "Iceberg-enriched-orders-aws-Java");
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment(config);

    StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

    env.enableCheckpointing(30000);

    String enrichedOrdersTemplate = "CREATE TABLE enriched_orders_cdc (\n" +
        "   order_id INT,\n" +
        "   order_date TIMESTAMP(3),\n" +
        "   customer_name STRING,\n" +
        "   price DECIMAL(10, 5),\n" +
        "   product_id INT,\n" +
        "   order_status BOOLEAN,\n" +
        "   product_name STRING,\n" +
        "   product_description STRING,\n" +
        "   shipment_id INT,\n" +
        "   origin STRING,\n" +
        "   destination STRING,\n" +
        "   is_arrived BOOLEAN,\n" +
        "   PRIMARY KEY (order_id) NOT ENFORCED ) \n" +
        " WITH (\n" +
        "   'connector' = 'mysql-cdc',\n" +
        "   'hostname' = '?',\n" +
        "   'port' = '?',\n" +
        "   'username' = '?',\n" +
        "   'password' = '?',\n" +
        "   'database-name' = '?',\n" +
        "   'table-name' = 'enriched_orders'\n" +
        ");\n";

    String enrichedOrdersSql = String.format(enrichedOrdersTemplate,
        jobConfig.get("mysql.opsdb.host.name"),
        jobConfig.get("mysql.opsdb.db.port"),
        jobConfig.get("mysql.opsdb.db.username"),
        jobConfig.get("mysql.opsdb.db.password"),
        jobConfig.get("mysql.opsdb.db.name"));

    tEnv.executeSql(enrichedOrdersSql);

    // since the iceberg catalog is persistent (unlike the session based in-mem catalog)
    // we have to check to see if the catalog and database already exist before
    // creating
    String catalogName = "iceberg_catalog";
    if (!Arrays.stream(tEnv.listCatalogs()).anyMatch(cat -> cat == catalogName)) {
      LOG.info("Iceberg catalog " + catalogName + " does not exist, creating...");
      String icebergCatalogTemplate = "CREATE CATALOG ? WITH (\n" +
          " 'type'='iceberg',\n" +
          " 'warehouse'='?',\n" +
          " 'catalog-impl'='org.apache.iceberg.aws.glue.GlueCatalog',\n" +
          " 'io-impl'='org.apache.iceberg.aws.s3.S3FileIO'\n" +
          ");";

      String icebergCatalogSql = String.format(icebergCatalogTemplate,
          catalogName,
          jobConfig.get("iceberg.s3.warehouse"));

      tEnv.executeSql(icebergCatalogSql);
    }
    tEnv.useCatalog(catalogName);

    String dbName = "iceberg_orders";
    if (!Arrays.stream(tEnv.listDatabases()).anyMatch(db -> db.equals(dbName))) {
      LOG.info("Iceberg database: " + dbName + " does not exist, creating...");
      tEnv.executeSql("CREATE DATABASE " + dbName + ";");
    }
    tEnv.useDatabase(dbName);

    String lakeTableName = "enriched_orders_lake";
    if (!Arrays.stream(tEnv.listTables()).anyMatch(t -> t.equals(lakeTableName))) {
      LOG.info("Iceberg lake table: " + lakeTableName + " does not exist, creating...");
      String tableSql = "CREATE TABLE " + lakeTableName + " (\n" +
          " order_id INT,\n" +
          " order_date TIMESTAMP,\n" +
          " customer_name STRING,\n" +
          " price DECIMAL(10, 5),\n" +
          " product_id INT,\n" +
          " order_status BOOLEAN,\n" +
          " product_name STRING,\n" +
          " product_description STRING,\n" +
          " shipment_id INT,\n" +
          " origin STRING,\n" +
          " destination STRING,\n" +
          " is_arrived BOOLEAN,\n" +
          " PRIMARY KEY (order_id) NOT ENFORCED );\n";

      tEnv.executeSql(tableSql);
    }

    // we'll try straight Flink SQL here
    String insertSql = "INSERT INTO iceberg_catalog.iceberg_orders.enriched_orders_lake " +
        " SELECT * FROM default_catalog.default_database.enriched_orders_cdc";

    LOG.info("Executing enriched orders streaming CDC UPSERT to Icerberg table: " + lakeTableName);
    tEnv.executeSql(insertSql);

    // Table API
    //
    // tEnv.useCatalog("default_catalog");
    // tEnv.useDatabase("default_database");
    // Table enrichedOrdersCDC = tEnv.from("enriched_orders_cdc");

    // enrichedOrdersCDC.insertInto("iceberg_catalog.iceberg_orders.enriched_orders_lake").execute();
  }
}
