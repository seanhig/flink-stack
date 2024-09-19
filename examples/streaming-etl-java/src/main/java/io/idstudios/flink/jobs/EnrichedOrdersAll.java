package io.idstudios.flink.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class is only used in docker-compose as K8s FlinkSessionJob spec includes the entry class specification.
// and running multiple jobs from a single class does not appear supported in the K8s operator.
// Note that this is the class specified in the Maven shade plugin as the main class for the manifest, which is largely 
// ignored by the FlinkSessionJob config
public class EnrichedOrdersAll {
      private static final Logger LOG = LoggerFactory.getLogger(EnrichedOrdersMySQL.class);
      public static void main(String[] args) throws Exception {

        LOG.info("Running ALL EnrichedOrders jobs...");

        EnrichedOrdersMySQL.main(args);
        EnrichedOrdersIceberg.main(args);
    }

}
