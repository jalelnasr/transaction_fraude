package com.frauddetection.decision.entity;

import com.frauddetection.common.enums.DecisionType;
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

import java.time.Instant;

@Entity
@Table(name = "fraud_decisions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudDecision {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecisionType status;

    @Column(name = "fused_score", nullable = false)
    private double fusedScore;

    @Column(name = "rule_score")
    private Double ruleScore;

    @Column(name = "ml_score")
    private Double mlScore;

    @Column(name = "degraded_mode", nullable = false)
    private boolean degradedMode;

    @Column(name = "decided_at", nullable = false)
    private Instant decidedAt;
}
