package com.aquacode.ctm.evaluation.infrastructure.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.Instant;

public record EvaluateTransactionRequest(
    @NotBlank
    String transactionId,
    @NotBlank
    String customerId,
    @NotBlank
    String country,
    @NotNull
    @DecimalMin(value = "0.01")
    BigDecimal amount,
    @NotNull
    Boolean politicallyExposedPerson,
    @NotNull
    @PastOrPresent
    Instant transactionTimestamp
) {
}
