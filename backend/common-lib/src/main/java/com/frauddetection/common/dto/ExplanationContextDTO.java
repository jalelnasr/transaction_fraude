package com.frauddetection.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplanationContextDTO {

    private List<TriggeredRuleDTO> triggeredRules;
    private List<InfluentialFeatureDTO> influentialFeatures;
    private double ruleScore;
    private double mlScore;
    private double fusedScore;
    private String decidingThreshold;
}
