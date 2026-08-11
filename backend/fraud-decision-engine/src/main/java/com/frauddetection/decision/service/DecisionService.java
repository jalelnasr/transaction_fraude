package com.frauddetection.decision.service;

import com.frauddetection.common.dto.DecisionDTO;
import com.frauddetection.common.dto.ExplanationContextDTO;
import com.frauddetection.common.dto.InfluentialFeatureDTO;
import com.frauddetection.common.dto.TriggeredRuleDTO;
import com.frauddetection.common.enums.DecisionType;
import com.frauddetection.decision.entity.FraudDecision;
import com.frauddetection.decision.kafka.DecisionProducer;
import com.frauddetection.decision.repository.DecisionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DecisionService {

    private final ScoreFusionService scoreFusionService;
    private final ThresholdService thresholdService;
    private final DecisionRepository decisionRepository;
    private final DecisionProducer decisionProducer;

    @Transactional
    public void decide(PendingScore pending) {
        boolean hasRule = pending.getRuleScore() != null;
        boolean hasMl = pending.getMlScore() != null;
        boolean degraded = !(hasRule && hasMl);

        double ruleScore = hasRule ? pending.getRuleScore().getRuleScore() : 0.0;
        double mlScore = hasMl ? pending.getMlScore().getFraudScore() : 0.0;

        double fusedScore;
        if (hasRule && hasMl) {
            fusedScore = scoreFusionService.fuse(ruleScore, mlScore);
        } else if (hasRule) {
            log.warn("ML score unavailable for transaction [{}], deciding in degraded mode using rule score only",
                    pending.getTransactionId());
            fusedScore = ruleScore;
        } else {
            log.warn("Rule score unavailable for transaction [{}], deciding in degraded mode using ML score only",
                    pending.getTransactionId());
            fusedScore = mlScore;
        }

        DecisionType status = thresholdService.decide(fusedScore);

        List<TriggeredRuleDTO> triggeredRules = hasRule
                ? pending.getRuleScore().getTriggeredRules()
                : Collections.emptyList();
        List<InfluentialFeatureDTO> influentialFeatures = hasMl
                ? pending.getMlScore().getInfluentialFeatures()
                : Collections.emptyList();

        ExplanationContextDTO explanationContext = ExplanationContextDTO.builder()
                .triggeredRules(triggeredRules)
                .influentialFeatures(influentialFeatures)
                .ruleScore(ruleScore)
                .mlScore(mlScore)
                .fusedScore(fusedScore)
                .decidingThreshold(thresholdService.describeThreshold(fusedScore))
                .build();

        Instant now = Instant.now();

        FraudDecision decision = FraudDecision.builder()
                .id(UUID.randomUUID().toString())
                .transactionId(pending.getTransactionId())
                .status(status)
                .fusedScore(fusedScore)
                .ruleScore(hasRule ? ruleScore : null)
                .mlScore(hasMl ? mlScore : null)
                .degradedMode(degraded)
                .decidedAt(now)
                .build();
        decisionRepository.save(decision);

        DecisionDTO decisionDTO = DecisionDTO.builder()
                .transactionId(pending.getTransactionId())
                .status(status)
                .fusedScore(fusedScore)
                .explanationContext(explanationContext)
                .decidedAt(now)
                .build();

        decisionProducer.publish(decisionDTO);
    }
}
