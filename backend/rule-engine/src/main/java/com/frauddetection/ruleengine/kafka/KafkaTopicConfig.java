package com.frauddetection.ruleengine.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String TRANSACTIONS_TOPIC = "transactions";
    public static final String RULE_SCORES_TOPIC = "rule-scores";

    @Bean
    public NewTopic ruleScoresTopic() {
        return TopicBuilder.name(RULE_SCORES_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
