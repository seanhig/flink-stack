package io.idstudios.flink.jobs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.java.utils.ParameterTool;

public class JobConfig {

  private Map<String, String> params = new HashMap<String, String>();
  private ParameterTool config = null;

  protected JobConfig(String filePath) throws IOException {
    this.config = ParameterTool.fromPropertiesFile(filePath);
  }

  protected void setEnvOverrideValue(String key) {
    String envKey = key.replace(".", "_").toUpperCase();
    if (System.getenv().containsKey(envKey)) {
      this.params.put(key, System.getenv().get(envKey));
    } else {
      this.params.put(key, this.config.get(key));
    }
  }

  public String get(String key) {
    return this.params.get(key);
  }

  public ParameterTool getConfigParams() {
    return this.config;
  }

  public static JobConfig fromPropertiesFile(String filePath) throws IOException {

    JobConfig jobConfig = new JobConfig(filePath);

    while(jobConfig.getConfigParams().getProperties().keys().asIterator().hasNext()) {
      String propKey = (String) jobConfig.getConfigParams().getProperties().keys().asIterator().next();
      jobConfig.setEnvOverrideValue(propKey);
    }

    return jobConfig;
  }

}
