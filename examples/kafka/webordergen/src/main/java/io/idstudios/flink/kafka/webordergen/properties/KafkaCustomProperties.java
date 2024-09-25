package io.idstudios.flink.kafka.webordergen.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("kafka")
public class KafkaCustomProperties {

    private String webOrdersTopic;
}