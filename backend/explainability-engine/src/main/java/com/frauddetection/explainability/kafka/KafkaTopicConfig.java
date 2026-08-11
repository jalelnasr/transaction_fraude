package com.frauddetection.explainability.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String DECISIONS_TOPIC = "decisions";
    public static final String EXPLANATIONS_TOPIC = "explanations";

    @Bean
    public NewTopic explanationsTopic() {
        return TopicBuilder.name(EXPLANATIONS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
