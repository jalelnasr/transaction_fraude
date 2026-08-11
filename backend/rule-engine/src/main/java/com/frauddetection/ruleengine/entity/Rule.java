package com.frauddetection.ruleengine.entity;

import com.frauddetection.ruleengine.enums.RuleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rule {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType type;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private double weight;

    @Column(name = "threshold_amount")
    private BigDecimal thresholdAmount;

    @Column(name = "risk_countries")
    private String riskCountries;

    @Column(name = "max_transactions_per_window")
    private Integer maxTransactionsPerWindow;

    @Column(name = "window_minutes")
    private Integer windowMinutes;

    @Column(name = "night_start_hour")
    private Integer nightStartHour;

    @Column(name = "night_end_hour")
    private Integer nightEndHour;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
