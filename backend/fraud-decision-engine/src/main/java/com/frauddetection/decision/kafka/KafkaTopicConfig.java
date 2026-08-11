package com.frauddetection.decision.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String RULE_SCORES_TOPIC = "rule-scores";
    public static final String ML_SCORES_TOPIC = "ml-scores";
    public static final String DECISIONS_TOPIC = "decisions";

    @Bean
    public NewTopic decisionsTopic() {
        return TopicBuilder.name(DECISIONS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
