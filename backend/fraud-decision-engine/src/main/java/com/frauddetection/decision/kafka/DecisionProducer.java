package com.frauddetection.decision.kafka;

import com.frauddetection.common.dto.DecisionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DecisionProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(DecisionDTO decisionDTO) {
        String transactionId = decisionDTO.getTransactionId();
        kafkaTemplate.send(KafkaTopicConfig.DECISIONS_TOPIC, transactionId, decisionDTO)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish decision for transaction [{}]", transactionId, ex);
                    } else {
                        log.info("Published decision [{}] (score={}) for transaction [{}]",
                                decisionDTO.getStatus(), decisionDTO.getFusedScore(), transactionId);
                    }
                });
    }
}
