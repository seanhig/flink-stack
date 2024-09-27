package io.idstudios.flink.jobs.weborders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.idstudios.flink.jobs.FlinkJob;
import io.idstudios.flink.jobs.JobConfig;
import io.idstudios.flink.models.WebOrder;

import java.util.Properties;

import org.apache.avro.generic.GenericRecord;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.avro.registry.confluent.ConfluentRegistryAvroDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.kafka.clients.consumer.ConsumerConfig;

public class WebOrdersMonitor extends FlinkJob {

  private static final Logger log = LoggerFactory.getLogger(WebOrdersMonitor.class);
  private static final String DEFAULT_CONFIG_PATH = "/flink-job-configs/weborder-jobs.properties";

  public static void main(String[] args) throws Exception {

    JobConfig jobConfig = loadJobConfig(WebOrdersMonitor.DEFAULT_CONFIG_PATH, args);

    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, jobConfig.get("bootstrap.servers"));
    props.put(ConsumerConfig.GROUP_ID_CONFIG, jobConfig.get("kafka.group.id"));
    //props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    //props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
    props.put("schema.registry.url", jobConfig.get("schema.registry.url"));
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, jobConfig.get("auto.offset.reset.config"));

    String topic = jobConfig.get("weborders.topic");

    KafkaSource<GenericRecord> webOrderSource = KafkaSource.<GenericRecord>builder()
        .setProperties(props)
        .setTopics(topic)
        .setBootstrapServers(jobConfig.get("bootstrap.servers"))
        .setStartingOffsets(OffsetsInitializer.latest())
        .setValueOnlyDeserializer(ConfluentRegistryAvroDeserializationSchema.forGeneric(WebOrder.getClassSchema(),
          jobConfig.get("schema.registry.url")))
        .build();

    DataStream<GenericRecord> webOrderStream = env.fromSource(webOrderSource,
        WatermarkStrategy.noWatermarks(),
        "weborders_source");

    webOrderStream.addSink(new CollectSink());

    env.execute("Flink-WebOrders-Monitor");

  }

  private static class CollectSink implements SinkFunction<GenericRecord> {

    @Override
    public void invoke(GenericRecord weborder, SinkFunction.Context context) throws Exception {
      log.info("weborder: " + weborder.toString());
    }
  }
}

/*
 * 
 * KafkaDeserializationSchema<WebOrder> deserializationSchema = new
 * KafkaDeserializationSchema<WebOrder>(){
 * 
 * DeserializationSchema<WebOrder> schema =
 * ConfluentRegistryAvroDeserializationSchema.forSpecific(WebOrder.class,
 * "http://host.docker.internal:8081");
 * 
 * @Override
 * public boolean isEndOfStream(WebOrder nextElement) {
 * return false;
 * }
 * 
 * @Override
 * public WebOrder deserialize(ConsumerRecord<byte[], byte[]> record) throws
 * Exception {
 * return (WebOrder) this.schema.deserialize(record.value());
 * }
 * 
 * @Override
 * public TypeInformation<WebOrder> getProducedType() {
 * return TypeInformation.of(WebOrder.class);
 * }
 * };
 * 
 * AvroDeserializationSchema<WebOrder> avroSchema = new
 * AvroDeserializationSchema<WebOrder>()){
 * 
 * DeserializationSchema<WebOrder> schema =
 * ConfluentRegistryAvroDeserializationSchema.forSpecific(WebOrder.class,
 * "http://host.docker.internal:8081");
 * 
 * @Override
 * public boolean isEndOfStream(WebOrder nextElement) {
 * return false;
 * }
 * 
 * @Override
 * public WebOrder deserialize(ConsumerRecord<byte[], byte[]> record) throws
 * Exception {
 * return (WebOrder) this.schema.deserialize(record.value());
 * }
 * 
 * @Override
 * public TypeInformation<WebOrder> getProducedType() {
 * return TypeInformation.of(WebOrder.class);
 * }
 * };
 */