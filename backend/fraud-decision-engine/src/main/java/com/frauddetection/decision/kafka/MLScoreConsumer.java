package com.frauddetection.decision.kafka;

import com.frauddetection.common.dto.MLScoreDTO;
import com.frauddetection.decision.service.ScoreCorrelationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MLScoreConsumer {

    private final ScoreCorrelationService scoreCorrelationService;

    @KafkaListener(
            topics = KafkaTopicConfig.ML_SCORES_TOPIC,
            groupId = "fraud-decision-engine-ml-group",
            containerFactory = "mlScoreListenerContainerFactory"
    )
    public void consume(MLScoreDTO mlScoreDTO) {
        log.info("Received ML score [{}] for transaction [{}]",
                mlScoreDTO.getFraudScore(), mlScoreDTO.getTransactionId());
        scoreCorrelationService.onMlScore(mlScoreDTO);
    }
}
