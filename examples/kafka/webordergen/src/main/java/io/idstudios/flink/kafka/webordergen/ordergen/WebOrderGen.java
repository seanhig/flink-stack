package io.idstudios.flink.kafka.webordergen.ordergen;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import io.idstudios.flink.kafka.webordergen.model.WebOrder;
import io.idstudios.flink.kafka.webordergen.properties.KafkaCustomProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebOrderGen {
    private final KafkaTemplate<String, WebOrder> kafkaTemplate;
    private final KafkaCustomProperties kafkaCustomProperties;

    private List<Integer> productList = Arrays.asList(101, 102, 103, 104, 105, 106, 107, 108, 109);
    private List<String> customerFirstNameList = Arrays.asList("Mickey",
            "Joseph",
            "Karl",
            "John",
            "Moses",
            "Harold",
            "Burger Meister",
            "Fred",
            "Marjorie",
            "Oliver");

    private List<String> customerLastNameList = Arrays.asList("Disney",
            "Rockefeller",
            "Johnson",
            "Barrymore",
            "Znaimer",
            "Reisen",
            "Von Vagreville",
            "Cooper",
            "Mckeon",
            "Flink");

    private List<String> destinationList = Arrays.asList("Paris, France",
            "Bejing, China",
            "London, England",
            "Moose Jaw, Canada",
            "Springfield, Ohio",
            "Austin, Texas",
            "Beirut, Lebanon",
            "Winnipeg, Manitoba",
            "Drumheller, Canada",
            "Madrid, Spain");

    private WebOrder getMockWebOrder() {

        Random r = new Random();

        int productId = this.productList.get(r.nextInt(this.productList.size()));
        String customerFirstName = this.customerFirstNameList.get(r.nextInt(this.customerFirstNameList.size()));
        String customerLastName = this.customerLastNameList.get(r.nextInt(this.customerLastNameList.size()));
        String destination = this.destinationList.get(r.nextInt(this.destinationList.size()));

        return WebOrder.newBuilder()
                .setWebOrderId(UUID.randomUUID().toString())
                .setProductId(productId)
                .setCustomerName(customerFirstName + " " + customerLastName)
                .setOrderDate(Instant.now().toEpochMilli())
                .setDestination(destination)
                .setQuantity(r.nextInt(100))
                .build();
    }

    public void generateMockWebOrders(int orderCount, int maxBatchSize, int pauseRange, int batchPauseRange) {
        if (orderCount <= 0) {
            return;
        }
        if (orderCount < maxBatchSize) {
            log.error("maxBatchSize must be smaller then the total order count!");
            return;
        }

        int count = orderCount;
        while (count > 0) {
            Random r = new Random();

            int pauseSecs = r.nextInt(pauseRange);
            int batchPauseSecs = r.nextInt(batchPauseRange);

            int batch = r.nextInt(maxBatchSize);
            if (batch > count) {
                batch = count;
            }

            log.info("generating batch of  [" + batch + "] web orders out of [" + count + "]");
            for (var i = 0; i < batch; i++) {
                this.sendMessage(getMockWebOrder());
                log.info("pausing for " + pauseSecs + " seconds between orders...");
                pause(pauseSecs);
                count--;
            }
            if (count > 0) {
                log.info("pausing between batches [" + batch + "] for " + pauseSecs + " seconds...");
                pause(batchPauseSecs);
            }
        }
        log.info("Generated [" + orderCount +"] web orders.");
    }

    private void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ex) {
            log.error("web order gen pause interrupted!", ex);
        }
    }

    public void sendMessage(WebOrder orderEvent) {
        ProducerRecord<String, WebOrder> producerRecord = new ProducerRecord<>(
                kafkaCustomProperties.getWebOrdersTopic(), orderEvent.getCustomerName(), orderEvent);
        CompletableFuture<SendResult<String, WebOrder>> completableFuture = kafkaTemplate.send(producerRecord);
        log.info("Sending kafka message on topic: {}", kafkaCustomProperties.getWebOrdersTopic());

        completableFuture.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Kafka message successfully sent on topic {} and value {}",
                        kafkaCustomProperties.getWebOrdersTopic(), result.getProducerRecord().value().toString());
            } else {
                log.error("An error occurred while sending kafka message for event with value {}", producerRecord);
            }
        });
    }
}
