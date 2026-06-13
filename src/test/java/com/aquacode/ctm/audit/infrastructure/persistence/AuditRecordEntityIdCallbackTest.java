package com.aquacode.ctm.audit.infrastructure.persistence;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuditRecordEntityIdCallbackTest {

    private final AuditRecordEntityIdCallback callback = new AuditRecordEntityIdCallback();

    @Test
    void onBeforeConvert_shouldAssignIdWhenMissing() {
        var entity = new AuditRecordEntity(
            null,
            "tx-1",
            "dec_1",
            "v1",
            "APPROVED",
            Instant.parse("2025-01-01T12:00:00Z")
        );

        var result = callback.onBeforeConvert(entity);

        assertThat(result.id()).isNotNull();
        assertThat(result.transactionId()).isEqualTo("tx-1");
        assertThat(result.decisionId()).isEqualTo("dec_1");
        assertThat(result.ruleSetVersion()).isEqualTo("v1");
        assertThat(result.decision()).isEqualTo("APPROVED");
        assertThat(result.timestamp()).isEqualTo(Instant.parse("2025-01-01T12:00:00Z"));
    }

    @Test
    void onBeforeConvert_shouldKeepExistingId() {
        var id = UUID.fromString("00000000-0000-0000-0000-000000000001");
        var entity = new AuditRecordEntity(
            id,
            "tx-1",
            "dec_1",
            "v1",
            "APPROVED",
            Instant.parse("2025-01-01T12:00:00Z")
        );

        var result = callback.onBeforeConvert(entity);

        assertThat(result.id()).isEqualTo(id);
    }
}