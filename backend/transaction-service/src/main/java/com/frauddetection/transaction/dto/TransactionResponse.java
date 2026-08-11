package com.frauddetection.transaction.dto;

import com.frauddetection.common.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String sourceAccountId;
    private String destinationAccountId;
    private String channel;
    private String country;
    private TransactionStatus status;
    private Instant timestamp;
}
