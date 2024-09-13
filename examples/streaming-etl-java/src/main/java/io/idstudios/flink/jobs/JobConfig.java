package io.idstudios.flink.jobs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobConfig {

  private static final Logger LOG = LoggerFactory.getLogger(JobConfig.class);
  private Map<String, String> params = new HashMap<String, String>();
  private ParameterTool config = null;

  protected JobConfig(String filePath) throws IOException {
    this.config = ParameterTool.fromPropertiesFile(filePath);
  }

  protected void setEnvOverrideValue(String key) {
    String envKey = key.replace(".", "_").toUpperCase();
    if (System.getenv().containsKey(envKey)) {
      LOG.info("loaded property {} as ENV override: {}", key, envKey);
      this.params.put(key, System.getenv().get(envKey));
    } else {
      LOG.info("loaded property {}", key);
      this.params.put(key, this.config.get(key));
    }
  }

  public String get(String key) throws Exception {
    String value = this.params.get(key);
    if(value == null) { throw new Exception(String.format("NULL found fetching property:  %s", key)); }
    return value;
  }

  public ParameterTool getConfigParams() {
    return this.config;
  }

  public static JobConfig fromPropertiesFile(String filePath) throws IOException {

    JobConfig jobConfig = new JobConfig(filePath);

    Iterator<Object> props = jobConfig.getConfigParams().getProperties().keys().asIterator();
    while(props.hasNext()) {
      String propKey = (String) props.next();
      jobConfig.setEnvOverrideValue(propKey);
    }

    return jobConfig;
  }

}
