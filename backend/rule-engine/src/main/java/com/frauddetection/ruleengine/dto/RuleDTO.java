package com.frauddetection.ruleengine.dto;

import com.frauddetection.ruleengine.enums.RuleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleDTO {

    private String id;

    @NotBlank
    private String name;

    @NotNull
    private RuleType type;

    private boolean active;

    @PositiveOrZero
    private double weight;

    private BigDecimal thresholdAmount;
    private String riskCountries;
    private Integer maxTransactionsPerWindow;
    private Integer windowMinutes;
    private Integer nightStartHour;
    private Integer nightEndHour;
}
