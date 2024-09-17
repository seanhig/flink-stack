package io.idstudios.flink.jobs;

import java.io.IOException;

import org.apache.flink.api.java.utils.ParameterTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FlinkJob {
  private static final Logger LOG = LoggerFactory.getLogger(FlinkJob.class);

  public static JobConfig loadJobConfig(String defaultConfigPath, String[] args) throws IOException {
    ParameterTool parameters = ParameterTool.fromArgs(args);
    if (parameters.has("config-filepath")) {
      defaultConfigPath = parameters.get("config-filepath");
      LOG.info("Loading config from specified PATH: " + defaultConfigPath);
    } else {
      LOG.info("Loading config from DEFAULT PATH (for k8s): " + defaultConfigPath);
    }

    try {
      return JobConfig.fromPropertiesFile(defaultConfigPath);
    } catch (Exception e) {
      LOG.error("failure attempting to load enriched orders job config from " + defaultConfigPath, e);
      throw e;
    }
  }

}
