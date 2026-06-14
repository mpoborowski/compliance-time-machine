package com.aquacode.ctm.audit.infrastructure.persistence;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static com.aquacode.ctm.audit.AuditFixtures.auditRecordEntity;
import static com.aquacode.ctm.audit.AuditFixtures.auditRecordEntityWithoutId;
import static org.assertj.core.api.Assertions.assertThat;

class AuditRecordEntityIdCallbackTest {

    private final AuditRecordEntityIdCallback callback = new AuditRecordEntityIdCallback();

    @Test
    void onBeforeConvert_shouldAssignIdWhenMissing() {
        var entity = auditRecordEntityWithoutId();

        var result = callback.onBeforeConvert(entity);

        assertThat(result.id()).isNotNull();
        assertThat(result.amount()).isEqualTo(BigDecimal.TEN);
        assertThat(result.customerId()).isEqualTo("customer-1");
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
        var entity = auditRecordEntity();

        var result = callback.onBeforeConvert(entity);

        assertThat(result.id()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    }
}