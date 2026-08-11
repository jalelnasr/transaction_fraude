package com.frauddetection.explainability.kafka;

import com.frauddetection.common.dto.ExplanationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExplanationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(ExplanationDTO explanationDTO) {
        String transactionId = explanationDTO.getTransactionId();
        kafkaTemplate.send(KafkaTopicConfig.EXPLANATIONS_TOPIC, transactionId, explanationDTO)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish explanation for transaction [{}]", transactionId, ex);
                    } else {
                        log.info("Published explanation for transaction [{}]", transactionId);
                    }
                });
    }
}
