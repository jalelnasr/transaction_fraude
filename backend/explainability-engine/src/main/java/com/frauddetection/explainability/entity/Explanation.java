package com.frauddetection.explainability.entity;

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
@Table(name = "explanations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Explanation {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String justification;

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;
}
