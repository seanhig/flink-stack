package io.idstudios.flink.jobs;

import java.io.IOException;

import org.apache.flink.api.java.utils.ParameterTool;

public class JobConfigItems {
  private String mysqlERPDBHostName;
  private String mysqlERPDBName;
  private String mysqlERPDBPort;
  private String mysqlERPDBUsername;
  private String mysqlERPDBPassword;

  private String postgresShipDBHostName;
  private String postgresShipSchemaName;
  private String postgresShipDBName;
  private String postgresShipDBPort;
  private String postgresShipDBUsername;
  private String postgresShipDBPassword;
  private String postgresShipDBSlotname;

  private String mysqlOpsDBHostName;
  private String mysqlOpsDBName;
  private String mysqlOpsDBPort;
  private String mysqlOpsDBUsername;
  private String mysqlOpsDBPassword;

  public static JobConfigItems fromPropertiesFile(String filePath) throws IOException {
        
    ParameterTool config = ParameterTool.fromPropertiesFile(filePath);

    JobConfigItems jobConfig = new JobConfigItems();
    jobConfig.setMysqlERPDBHostName(config.get("mysql.erpdb.host.name"));
    jobConfig.setMysqlERPDBName(config.get("mysql.erpdb.db.name"));
    jobConfig.setMysqlERPDBPort(config.get("mysql.erpdb.db.port"));
    jobConfig.setMysqlERPDBUsername(config.get("mysql.erpdb.db.user"));
    jobConfig.setMysqlERPDBPassword(config.get("mysql.erpdb.db.password"));

    jobConfig.setPostgresShipDBHostName(config.get("postgres.shipdb.host.name"));
    jobConfig.setPostgresShipSchemaName(config.get("postgres.shipdb.schema.name"));
    jobConfig.setPostgresShipDBName(config.get("postgres.shipdb.db.name"));
    jobConfig.setPostgresShipDBPort(config.get("postgres.shipdb.db.port"));
    jobConfig.setPostgresShipDBUsername(config.get("postgres.shipdb.db.user"));
    jobConfig.setPostgresShipDBPassword(config.get("postgres.shipdb.db.password"));
    jobConfig.setPostgresShipDBSlotname(config.get("postgres.shipdb.db.slotname"));

    jobConfig.setMysqlOpsDBHostName(config.get("mysql.opsdb.host.name"));
    jobConfig.setMysqlOpsDBName(config.get("mysql.opsdb.db.name"));
    jobConfig.setMysqlOpsDBPort(config.get("mysql.opsdb.db.port"));
    jobConfig.setMysqlOpsDBUsername(config.get("mysql.opsdb.db.user"));
    jobConfig.setMysqlOpsDBPassword(config.get("mysql.opsdb.db.password"));

    return jobConfig;
  }

  public String getMysqlERPDBHostName() {
    return mysqlERPDBHostName;
  }
  public void setMysqlERPDBHostName(String mysqlERPDBHostName) {
    this.mysqlERPDBHostName = mysqlERPDBHostName;
  }
  public String getMysqlERPDBName() {
    return mysqlERPDBName;
  }
  public void setMysqlERPDBName(String mysqlERPDBName) {
    this.mysqlERPDBName = mysqlERPDBName;
  }
  public String getMysqlERPDBPort() {
    return mysqlERPDBPort;
  }
  public void setMysqlERPDBPort(String mysqlERPDBPort) {
    this.mysqlERPDBPort = mysqlERPDBPort;
  }
  public String getMysqlERPDBUsername() {
    return mysqlERPDBUsername;
  }
  public void setMysqlERPDBUsername(String mysqlERPDBUsername) {
    this.mysqlERPDBUsername = mysqlERPDBUsername;
  }
  public String getMysqlERPDBPassword() {
    // pull the secret from the environment
    return System.getenv(mysqlERPDBHostName);
  }
  public void setMysqlERPDBPassword(String mysqlERPDBPassword) {
    this.mysqlERPDBPassword = mysqlERPDBPassword;
  }
  public String getPostgresShipDBHostName() {
    return postgresShipDBHostName;
  }
  public void setPostgresShipDBHostName(String postgresShipDBHostName) {
    this.postgresShipDBHostName = postgresShipDBHostName;
  }
  public String getPostgresShipDBName() {
    return postgresShipDBName;
  }
  public void setPostgresShipDBName(String postgresShipDBName) {
    this.postgresShipDBName = postgresShipDBName;
  }
  public String getPostgresShipSchemaName() {
    return postgresShipSchemaName;
  }
  public String getPostgresShipDBSlotname() {
    return postgresShipDBSlotname;
  }

  public void setPostgresShipDBSlotname(String postgresShipDBSlotname) {
    this.postgresShipDBSlotname = postgresShipDBSlotname;
  }

  public void setPostgresShipSchemaName(String postgresShipSchemaName) {
    this.postgresShipSchemaName = postgresShipSchemaName;
  }
  public String getPostgresShipDBPort() {
    return postgresShipDBPort;
  }
  public void setPostgresShipDBPort(String postgresShipDBPort) {
    this.postgresShipDBPort = postgresShipDBPort;
  }
  public String getPostgresShipDBUsername() {
    return postgresShipDBUsername;
  }
  public void setPostgresShipDBUsername(String postgresShipDBUsername) {
    this.postgresShipDBUsername = postgresShipDBUsername;
  }
  public String getPostgresShipDBPassword() {
    return System.getenv().get(postgresShipDBPassword);
  }
  public void setPostgresShipDBPassword(String postgresShipDBPassword) {
    this.postgresShipDBPassword = postgresShipDBPassword;
  }
  public String getMysqlOpsDBHostName() {
    return mysqlOpsDBHostName;
  }
  public void setMysqlOpsDBHostName(String mysqlOpsDBHostName) {
    this.mysqlOpsDBHostName = mysqlOpsDBHostName;
  }
  public String getMysqlOpsDBName() {
    return mysqlOpsDBName;
  }
  public void setMysqlOpsDBName(String mysqlOpsDBName) {
    this.mysqlOpsDBName = mysqlOpsDBName;
  }
  public String getMysqlOpsDBPort() {
    return mysqlOpsDBPort;
  }
  public void setMysqlOpsDBPort(String mysqlOpsDBPort) {
    this.mysqlOpsDBPort = mysqlOpsDBPort;
  }
  public String getMysqlOpsDBUsername() {
    return mysqlOpsDBUsername;
  }
  public void setMysqlOpsDBUsername(String mysqlOpsDBUsername) {
    this.mysqlOpsDBUsername = mysqlOpsDBUsername;
  }
  public String getMysqlOpsDBPassword() {
    return System.getenv().get(mysqlOpsDBPassword);
  }
  public void setMysqlOpsDBPassword(String mysqlOpsDBPassword) {
    this.mysqlOpsDBPassword = mysqlOpsDBPassword;
  }

}
