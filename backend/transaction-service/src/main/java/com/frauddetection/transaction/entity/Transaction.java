package com.frauddetection.transaction.entity;

import com.frauddetection.common.enums.TransactionStatus;
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
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "source_account_id", nullable = false)
    private String sourceAccountId;

    @Column(name = "destination_account_id", nullable = false)
    private String destinationAccountId;

    @Column(nullable = false)
    private String channel;

    private String country;

    private String city;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "source_balance_before")
    private BigDecimal sourceBalanceBefore;

    @Column(name = "source_balance_after")
    private BigDecimal sourceBalanceAfter;

    @Column(name = "destination_balance_before")
    private BigDecimal destinationBalanceBefore;

    @Column(name = "destination_balance_after")
    private BigDecimal destinationBalanceAfter;

    @Column(nullable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
