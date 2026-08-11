package com.frauddetection.decision.config;

import com.frauddetection.common.dto.MLScoreDTO;
import com.frauddetection.common.dto.RuleScoreDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DecisionEngineConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConsumerProps(String groupId, Class<?> defaultType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.frauddetection.common.dto");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, defaultType.getName());
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return props;
    }

    @Bean
    public ConsumerFactory<String, RuleScoreDTO> ruleScoreConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(baseConsumerProps("fraud-decision-engine-rule-group", RuleScoreDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RuleScoreDTO> ruleScoreListenerContainerFactory(
            ConsumerFactory<String, RuleScoreDTO> ruleScoreConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, RuleScoreDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(ruleScoreConsumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, MLScoreDTO> mlScoreConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(baseConsumerProps("fraud-decision-engine-ml-group", MLScoreDTO.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MLScoreDTO> mlScoreListenerContainerFactory(
            ConsumerFactory<String, MLScoreDTO> mlScoreConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, MLScoreDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(mlScoreConsumerFactory);
        return factory;
    }
}
