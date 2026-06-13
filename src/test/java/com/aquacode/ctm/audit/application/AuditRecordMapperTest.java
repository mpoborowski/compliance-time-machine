package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.shared.DecisionMadeEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AuditRecordMapperTest {

    private final AuditRecordMapper mapper = Mappers.getMapper(AuditRecordMapper.class);

    @Test
    void fromDecisionMadeEvent_shouldMapEventToAuditRecordEntity() {
        var evaluatedAt = Instant.parse("2025-01-01T12:00:00Z");
        var event = new DecisionMadeEvent(
            "tx-1",
            "dec_1",
            Decision.APPROVED.name(),
            "v1",
            evaluatedAt
        );

        var entity = mapper.fromDecisionMadeEvent(event);

        assertThat(entity.id()).isNull();
        assertThat(entity.transactionId()).isEqualTo("tx-1");
        assertThat(entity.decisionId()).isEqualTo("dec_1");
        assertThat(entity.ruleSetVersion()).isEqualTo("v1");
        assertThat(entity.decision()).isEqualTo(Decision.APPROVED.name());
        assertThat(entity.timestamp()).isEqualTo(evaluatedAt);
    }
}