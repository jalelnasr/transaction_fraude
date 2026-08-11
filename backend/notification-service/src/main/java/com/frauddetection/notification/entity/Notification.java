package com.frauddetection.notification.entity;

import com.frauddetection.common.enums.DecisionType;
import com.frauddetection.notification.enums.AlertStatus;
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
@Table(name = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_status", nullable = false)
    private DecisionType decisionStatus;

    @Column(name = "fused_score", nullable = false)
    private double fusedScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_status", nullable = false)
    private AlertStatus alertStatus;

    @Column(name = "email_sent", nullable = false)
    private boolean emailSent;

    @Column(name = "resolved_by")
    private String resolvedBy;

    @Column(name = "resolution_reason", columnDefinition = "TEXT")
    private String resolutionReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
