package com.frauddetection.ruleengine.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "rule_executions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleExecution {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "source_account_id", nullable = false)
    private String sourceAccountId;

    @Column(name = "rule_score", nullable = false)
    private double ruleScore;

    @Column(name = "evaluated_at", nullable = false)
    private Instant evaluatedAt;
}
