package io.idstudios.flink.jobs;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.flink.api.java.utils.ParameterTool;

public class FlinkJob {
    private static final Logger log = LoggerFactory.getLogger(FlinkJob.class);

    protected static JobConfig loadJobConfig(String defaultConfigPath, String[] args) throws IOException {
        ParameterTool parameters = ParameterTool.fromArgs(args);
        if (parameters.has("config-filepath")) {
            defaultConfigPath = parameters.get("config-filepath");
            log.info("Loading config from specified PATH: " + defaultConfigPath);
        } else {
            log.info("Loading config from DEFAULT PATH (for k8s): " + defaultConfigPath);
        }

        try {
            return JobConfig.fromPropertiesFile(defaultConfigPath);
        } catch (Exception e) {
            log.error("failure attempting to load enriched orders job config from " + defaultConfigPath, e);
            throw e;
        }
    }

}
