package io.idstudios.flink.kafka.webordergen;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan("io.idstudios.flink.kafka.webordergen.properties")
public class WebOrderGenApplication {

	public static void main(String[] args) {
//		log.info("STARTING THE APPLICATION");
		SpringApplication.run(WebOrderGenApplication.class, args);
//		log.info("FINISHED THE APPLICATION");
	}

	@Bean
	public NewTopic topic() {
		log.info("Creating Kafka topic if required.");
		return TopicBuilder.name("weborders")
				.partitions(3)
				.replicas(1)
				.build();
	}

}
