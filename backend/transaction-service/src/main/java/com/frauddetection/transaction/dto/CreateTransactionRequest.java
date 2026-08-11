package com.frauddetection.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequest {

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal amount;

    @NotBlank
    private String currency;

    @NotBlank
    private String sourceAccountId;

    @NotBlank
    private String destinationAccountId;

    @NotBlank
    private String channel;

    private String country;

    private String city;

    private String customerId;

    private Instant timestamp;

    private BigDecimal sourceBalanceBefore;
    private BigDecimal sourceBalanceAfter;
    private BigDecimal destinationBalanceBefore;
    private BigDecimal destinationBalanceAfter;
}
