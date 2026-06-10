package com.aquacode.ctm.evaluation;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(
    String transactionId,
    String customerId,
    String country,
    BigDecimal amount,
    boolean politicallyExposedPerson,
    Instant timestamp
) {
}
