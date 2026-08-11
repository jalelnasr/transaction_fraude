package com.frauddetection.transaction.kafka;

import com.frauddetection.common.dto.DecisionDTO;
import com.frauddetection.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DecisionConsumer {

    private final TransactionService transactionService;

    @KafkaListener(
            topics = KafkaTopicConfig.DECISIONS_TOPIC,
            groupId = "transaction-service-decision-group",
            containerFactory = "decisionKafkaListenerContainerFactory"
    )
    public void consume(DecisionDTO decisionDTO) {
        log.info("Received decision [{}] for transaction [{}], syncing transaction status",
                decisionDTO.getStatus(), decisionDTO.getTransactionId());
        transactionService.applyDecisionStatus(decisionDTO.getTransactionId(), decisionDTO.getStatus());
    }
}
