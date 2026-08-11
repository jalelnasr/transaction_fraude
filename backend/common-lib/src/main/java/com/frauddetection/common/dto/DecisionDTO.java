package com.frauddetection.common.dto;

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
public class DecisionDTO {

    private String transactionId;
    private DecisionType status;
    private double fusedScore;
    private ExplanationContextDTO explanationContext;
    private Instant decidedAt;
}
