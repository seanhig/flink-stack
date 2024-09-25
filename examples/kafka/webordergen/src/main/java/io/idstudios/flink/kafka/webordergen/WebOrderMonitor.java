package io.idstudios.flink.kafka.webordergen;

import org.springframework.messaging.Message;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import io.idstudios.flink.kafka.webordergen.model.WebOrder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebOrderMonitor {

  public final static String LISTENER_ID = "weborderslistener";
  int messageCount = 0;

  @KafkaListener(id="weborderslistener", 
    topics = "${kafka.weborders-topic}", 
    containerFactory = "kafkaListenerContainerFactory", 
    groupId = "${spring.kafka.consumer.group-id}",
    autoStartup = "false")
  public void listen(Message<WebOrder> webOrderEventMessage) {
    log.info("WebOrder Arrived: - {}", webOrderEventMessage.toString());
    messageCount++;
    log.info("FETCHED [" + messageCount + "] orders this session.");
  }
}
