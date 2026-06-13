package com.aquacode.ctm.audit.infrastructure.persistence;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuditRecordEntityIdCallbackTest {

    private final AuditRecordEntityIdCallback callback = new AuditRecordEntityIdCallback();

    @Test
    void onBeforeConvert_shouldAssignIdWhenMissing() {
        var entity = AuditRecordEntity.builder()
            .id(null)
            .amount(BigDecimal.TEN)
            .transactionId("tx-1")
            .customerId("customer_1")
            .timestamp(Instant.parse("2025-01-01T12:00:00Z"))
            .country("PL")
            .transactionTimestamp(Instant.parse("2025-01-01T12:00:00Z"))
            .politicallyExposedPerson(false)
            .decisionId("dec_1")
            .ruleSetVersion("v1")
            .decision("APPROVED")
            .build();

        var result = callback.onBeforeConvert(entity);

        assertThat(result.id()).isNotNull();
        assertThat(result.amount()).isEqualTo(BigDecimal.TEN);
        assertThat(result.customerId()).isEqualTo("customer_1");
        assertThat(result.country()).isEqualTo("PL");
        assertThat(result.politicallyExposedPerson()).isFalse();
        assertThat(result.transactionId()).isEqualTo("tx-1");
        assertThat(result.decisionId()).isEqualTo("dec_1");
        assertThat(result.ruleSetVersion()).isEqualTo("v1");
        assertThat(result.decision()).isEqualTo("APPROVED");
        assertThat(result.timestamp()).isEqualTo(Instant.parse("2025-01-01T12:00:00Z"));
    }

    @Test
    void onBeforeConvert_shouldKeepExistingId() {
        var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var entity = AuditRecordEntity.builder()
            .id(id)
            .amount(BigDecimal.TEN)
            .customerId("customer_1")
            .timestamp(Instant.parse("2025-01-01T12:00:00Z"))
            .country("PL")
            .transactionTimestamp(Instant.parse("2025-01-01T12:00:00Z"))
            .politicallyExposedPerson(false)
            .decisionId("dec_1")
            .ruleSetVersion("v1")
            .decision("APPROVED")
            .build();

        var result = callback.onBeforeConvert(entity);

        assertThat(result.id()).isEqualTo(id);
    }
}