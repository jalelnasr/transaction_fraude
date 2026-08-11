package com.frauddetection.transaction.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String TRANSACTIONS_TOPIC = "transactions";
    public static final String DECISIONS_TOPIC = "decisions";

    @Bean
    public NewTopic transactionsTopic() {
        return TopicBuilder.name(TRANSACTIONS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
