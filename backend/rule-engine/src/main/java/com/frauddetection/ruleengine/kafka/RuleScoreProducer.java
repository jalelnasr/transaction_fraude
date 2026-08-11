package com.frauddetection.ruleengine.kafka;

import com.frauddetection.common.dto.RuleScoreDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RuleScoreProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(RuleScoreDTO ruleScoreDTO) {
        String transactionId = ruleScoreDTO.getTransactionId();
        kafkaTemplate.send(KafkaTopicConfig.RULE_SCORES_TOPIC, transactionId, ruleScoreDTO)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish rule score for transaction [{}]", transactionId, ex);
                    } else {
                        log.info("Published rule score [{}] for transaction [{}]",
                                ruleScoreDTO.getRuleScore(), transactionId);
                    }
                });
    }
}
