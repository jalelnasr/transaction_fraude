package com.frauddetection.decision.service;

import com.frauddetection.common.dto.MLScoreDTO;
import com.frauddetection.common.dto.RuleScoreDTO;
import lombok.Data;

import java.time.Instant;

@Data
public class PendingScore {

    private final String transactionId;
    private final Instant firstSeenAt = Instant.now();
    private RuleScoreDTO ruleScore;
    private MLScoreDTO mlScore;

    public PendingScore(String transactionId) {
        this.transactionId = transactionId;
    }

    public boolean isComplete() {
        return ruleScore != null && mlScore != null;
    }
}
