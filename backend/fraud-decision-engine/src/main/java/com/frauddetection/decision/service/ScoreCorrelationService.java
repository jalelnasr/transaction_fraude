package com.frauddetection.decision.service;

import com.frauddetection.common.dto.MLScoreDTO;
import com.frauddetection.common.dto.RuleScoreDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoreCorrelationService {

    private final DecisionService decisionService;

    @Value("${decision.correlation-timeout-seconds:5}")
    private long correlationTimeoutSeconds;

    private final Map<String, PendingScore> pendingScores = new ConcurrentHashMap<>();

    public void onRuleScore(RuleScoreDTO ruleScoreDTO) {
        PendingScore pending = pendingScores.computeIfAbsent(
                ruleScoreDTO.getTransactionId(), PendingScore::new);
        pending.setRuleScore(ruleScoreDTO);
        completeIfReady(pending);
    }

    public void onMlScore(MLScoreDTO mlScoreDTO) {
        PendingScore pending = pendingScores.computeIfAbsent(
                mlScoreDTO.getTransactionId(), PendingScore::new);
        pending.setMlScore(mlScoreDTO);
        completeIfReady(pending);
    }

    private void completeIfReady(PendingScore pending) {
        if (pending.isComplete()) {
            pendingScores.remove(pending.getTransactionId());
            decisionService.decide(pending);
        }
    }

    @Scheduled(fixedDelay = 2000)
    public void sweepTimedOutScores() {
        Instant cutoff = Instant.now().minus(Duration.ofSeconds(correlationTimeoutSeconds));
        Iterator<Map.Entry<String, PendingScore>> iterator = pendingScores.entrySet().iterator();

        while (iterator.hasNext()) {
            PendingScore pending = iterator.next().getValue();
            if (!pending.isComplete() && pending.getFirstSeenAt().isBefore(cutoff)) {
                iterator.remove();
                log.warn("Correlation timeout for transaction [{}], deciding in degraded mode", pending.getTransactionId());
                decisionService.decide(pending);
            }
        }
    }
}
