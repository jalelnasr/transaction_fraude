package com.frauddetection.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MLScoreDTO {

    private String transactionId;
    private double fraudScore;
    private String modelVersion;
    private List<InfluentialFeatureDTO> influentialFeatures;
    private Instant evaluatedAt;
}
