package com.frauddetection.decision.kafka;

import com.frauddetection.common.dto.RuleScoreDTO;
import com.frauddetection.decision.service.ScoreCorrelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RuleScoreConsumer {

    private final ScoreCorrelationService scoreCorrelationService;

    @KafkaListener(
            topics = KafkaTopicConfig.RULE_SCORES_TOPIC,
            groupId = "fraud-decision-engine-rule-group",
            containerFactory = "ruleScoreListenerContainerFactory"
    )
    public void consume(RuleScoreDTO ruleScoreDTO) {
        log.info("Received rule score [{}] for transaction [{}]",
                ruleScoreDTO.getRuleScore(), ruleScoreDTO.getTransactionId());
        scoreCorrelationService.onRuleScore(ruleScoreDTO);
    }
}
