package com.frauddetection.explainability.kafka;

import com.frauddetection.common.dto.DecisionDTO;
import com.frauddetection.explainability.service.ExplanationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DecisionConsumer {

    private final ExplanationService explanationService;

    @KafkaListener(
            topics = KafkaTopicConfig.DECISIONS_TOPIC,
            groupId = "explainability-engine-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(DecisionDTO decisionDTO) {
        log.info("Received decision [{}] for transaction [{}]",
                decisionDTO.getStatus(), decisionDTO.getTransactionId());
        explanationService.handleDecision(decisionDTO);
    }
}
