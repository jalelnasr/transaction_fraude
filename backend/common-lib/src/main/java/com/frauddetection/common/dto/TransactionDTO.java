package com.frauddetection.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TransactionDTO {

    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String sourceAccountId;
    private String destinationAccountId;
    private String channel;
    private String country;
    private String city;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;

    private String customerId;

    private BigDecimal sourceBalanceBefore;
    private BigDecimal sourceBalanceAfter;
    private BigDecimal destinationBalanceBefore;
    private BigDecimal destinationBalanceAfter;
}
