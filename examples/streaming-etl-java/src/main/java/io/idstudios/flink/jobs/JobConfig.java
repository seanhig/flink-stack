package io.idstudios.flink.jobs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.java.utils.ParameterTool;

public class JobConfig {

  private Map<String, String> params = new HashMap<String, String>();
  private ParameterTool config = null;

  protected JobConfig(String filePath)  throws IOException {
    this.config = ParameterTool.fromPropertiesFile(filePath);
  }

  protected void setEnvOverrideValue(String key) {
    String envKey = key.replace(".", "_").toUpperCase();
    if(System.getenv().containsKey(envKey)) {
      this.params.put(key, System.getenv().get(envKey));
    } else {
      this.params.put(key, this.config.get(key));
    }
  }

  public String get(String key) {
    return this.params.get(key);
  }

  public static JobConfig fromPropertiesFile(String filePath) throws IOException {
        
    JobConfig jobConfig = new JobConfig(filePath);

    jobConfig.setEnvOverrideValue("mysql.erpdb.host.name");
    jobConfig.setEnvOverrideValue("mysql.erpdb.db.name");
    jobConfig.setEnvOverrideValue("mysql.erpdb.db.port");
    jobConfig.setEnvOverrideValue("mysql.erpdb.db.user");
    jobConfig.setEnvOverrideValue("mysql.erpdb.db.password");

    jobConfig.setEnvOverrideValue("postgres.shipdb.host.name");
    jobConfig.setEnvOverrideValue("postgres.shipdb.schema.name");
    jobConfig.setEnvOverrideValue("postgres.shipdb.db.name");
    jobConfig.setEnvOverrideValue("postgres.shipdb.db.port");
    jobConfig.setEnvOverrideValue("postgres.shipdb.db.user");
    jobConfig.setEnvOverrideValue("postgres.shipdb.db.password");
    jobConfig.setEnvOverrideValue("postgres.shipdb.db.slotname");

    jobConfig.setEnvOverrideValue("mysql.opsdb.host.name");
    jobConfig.setEnvOverrideValue("mysql.opsdb.db.name");
    jobConfig.setEnvOverrideValue("mysql.opsdb.db.port");
    jobConfig.setEnvOverrideValue("mysql.opsdb.db.user");
    jobConfig.setEnvOverrideValue("mysql.opsdb.db.password");

    return jobConfig;
  }



}
