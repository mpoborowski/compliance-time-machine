package com.aquacode.ctm.audit;

import com.aquacode.ctm.evaluation.Decision;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record HistoricalTransactionAuditRecord(
    String transactionId,
    String customerId,
    String country,
    BigDecimal amount,
    boolean politicallyExposedPerson,
    Instant transactionTimestamp,
    String decisionId,
    String ruleSetVersion,
    Decision decision,
    Instant evaluatedAt
) {
}
