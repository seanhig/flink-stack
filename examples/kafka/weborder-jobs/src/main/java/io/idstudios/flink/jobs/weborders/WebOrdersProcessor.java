package io.idstudios.flink.jobs.weborders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.idstudios.flink.jobs.FlinkJob;
import io.idstudios.flink.jobs.JobConfig;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class WebOrdersProcessor extends FlinkJob {

    private static final Logger log = LoggerFactory.getLogger(WebOrdersProcessor.class);
    private static final String DEFAULT_CONFIG_PATH = "/flink-job-configs/weborder-jobs.properties";

    public static void main(String[] args) throws Exception {

        JobConfig jobConfig = loadJobConfig(WebOrdersProcessor.DEFAULT_CONFIG_PATH, args);

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "flink-weborder-processor");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        String topic = "weborders";
        
        final Consumer<String, GenericRecord> consumer = new KafkaConsumer<String, GenericRecord>(props);
        consumer.subscribe(Arrays.asList(topic));

        try {
            while (true) {
                ConsumerRecords<String, GenericRecord> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, GenericRecord> record : records) {
                    log.info(String.format("offset = %d, key = %s, value = %s \n", 
                        record.offset(), 
                        record.key(), 
                        record.value()));
                }
            }
        } finally {
            consumer.close();
        }

        /*
        Properties consumerConfig = new Properties();
        try (InputStream stream = WebOrdersProcessor.class.getResourceAsStream("kafka-consumer.properties")) {
            consumerConfig.load(stream);
        }

        KafkaSource<WebOrder> webOrderSource = KafkaSource.<WebOrder>builder()
            .setProperties(consumerConfig)
            .setTopics("weborders")
            .setStartingOffsets(OffsetsInitializer.latest())
            .setValueOnlyDeserializer(new AvroDeserializationSchema<WebOrder>(WebOrder.class,))
            .build();
         */
    }


}
