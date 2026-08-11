package com.frauddetection.ruleengine.kafka;

import com.frauddetection.common.dto.RuleScoreDTO;
import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.service.RuleEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionConsumer {

    private final RuleEvaluationService ruleEvaluationService;
    private final RuleScoreProducer ruleScoreProducer;

    @KafkaListener(
            topics = KafkaTopicConfig.TRANSACTIONS_TOPIC,
            groupId = "rule-engine-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(TransactionDTO transaction) {
        log.info("Received transaction [{}] for rule evaluation", transaction.getTransactionId());
        RuleScoreDTO ruleScore = ruleEvaluationService.evaluate(transaction);
        ruleScoreProducer.publish(ruleScore);
    }
}
