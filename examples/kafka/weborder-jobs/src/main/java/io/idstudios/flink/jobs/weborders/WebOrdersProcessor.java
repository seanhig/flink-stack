package io.idstudios.flink.jobs.weborders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.idstudios.flink.jobs.FlinkJob;
import io.idstudios.flink.jobs.JobConfig;

public class WebOrdersProcessor extends FlinkJob {

    private static final Logger log = LoggerFactory.getLogger(WebOrdersProcessor.class);
    private static final String DEFAULT_CONFIG_PATH = "/flink-job-configs/weborders-jobs.properties";

    public static void main(String[] args) throws Exception {

        JobConfig jobConfig = loadJobConfig(WebOrdersProcessor.DEFAULT_CONFIG_PATH, args);

    }


}
