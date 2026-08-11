package com.frauddetection.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TriggeredRuleDTO {

    private String ruleId;
    private String ruleName;
    private double weight;
    private String reason;
}
