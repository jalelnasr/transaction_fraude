package com.frauddetection.decision.dto;

import com.frauddetection.common.enums.DecisionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionResponse {

    private String transactionId;
    private DecisionType status;
    private double fusedScore;
    private Double ruleScore;
    private Double mlScore;
    private boolean degradedMode;
    private Instant decidedAt;
}
