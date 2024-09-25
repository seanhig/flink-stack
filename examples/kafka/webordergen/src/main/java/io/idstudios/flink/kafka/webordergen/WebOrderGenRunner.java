package io.idstudios.flink.kafka.webordergen;

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import io.idstudios.flink.kafka.webordergen.ordergen.WebOrderGen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebOrderGenRunner implements CommandLineRunner {
  private final WebOrderGen kafkaOrderGen;

  @Autowired
  KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

  @Override
  public void run(String... args) {

    try {
      WorkOrderGenOptions opts = WorkOrderGenOptions.getOpts(args);
      log.info("OPTIONS: {}", opts.toString());

      if (opts.isGenerator()) {
        log.info("GENERATOR: Generating Mock WebOrder kafka messages...");
        kafkaOrderGen.generateMockWebOrders(opts.getOrderCount(),
            opts.getBatchSizeMax(),
            opts.getOrderPauseMax(),
            opts.getBatchPauseMax());
      } else {
        // Monitor mode
        log.info("MONITOR: Fetching Mock WebOrder kafka messages...");
        this.startListener(WebOrderMonitor.LISTENER_ID);
      }
    } catch (UnrecognizedOptionException oex) {
      log.error(oex.getMessage());
    } catch (ParseException pex) {
      log.error("Error parsing CLI options: {}", pex.getMessage());
    }

  }

  private boolean startListener(String listenerId) {
    MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
    assert listenerContainer != null : false;
    listenerContainer.start();
    log.info("{} Kafka Listener Started", listenerId);
    return true;
  }

  private boolean stopListener(String listenerId) {
    MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
    assert listenerContainer != null : false;
    listenerContainer.stop();
    log.info("{} Kafka Listener Stopped.", listenerId);
    return true;
  }
}
