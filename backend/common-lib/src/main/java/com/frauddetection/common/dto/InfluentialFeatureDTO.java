package com.frauddetection.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfluentialFeatureDTO {

    private String featureName;
    private double importanceScore;
}
