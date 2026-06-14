package com.aquacode.ctm.audit;

import com.aquacode.ctm.audit.infrastructure.persistence.AuditRecordEntity;
import com.aquacode.ctm.evaluation.Decision;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class AuditFixtures {

    public static AuditRecordEntity auditRecordEntity() {
        return AuditRecordEntity.builder()
            .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
            .transactionId("tx-1")
            .customerId("customer-1")
            .country("PL")
            .amount(BigDecimal.TEN)
            .politicallyExposedPerson(false)
            .transactionTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
            .decisionId("dec_1")
            .ruleSetVersion("v1")
            .decision(Decision.APPROVED.name())
            .timestamp(Instant.parse("2025-01-01T12:00:00Z"))
            .build();
    }

    public static AuditRecordEntity auditRecordEntityWithoutId() {
        return AuditRecordEntity.builder()
            .transactionId("tx-1")
            .customerId("customer-1")
            .country("PL")
            .amount(BigDecimal.TEN)
            .politicallyExposedPerson(false)
            .transactionTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
            .decisionId("dec_1")
            .ruleSetVersion("v1")
            .decision(Decision.APPROVED.name())
            .timestamp(Instant.parse("2025-01-01T12:00:00Z"))
            .build();
    }

    public static HistoricalTransactionAuditRecord historicalTransactionAuditRecord() {
        return HistoricalTransactionAuditRecord.builder()
            .transactionId("tx-1")
            .customerId("customer-1")
            .country("PL")
            .amount(BigDecimal.TEN)
            .politicallyExposedPerson(false)
            .transactionTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
            .decisionId("dec_1")
            .ruleSetVersion("v1")
            .decision(Decision.APPROVED)
            .evaluatedAt(Instant.parse("2025-01-01T12:00:00Z"))
            .build();
    }
}
