package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.shared.DecisionMadeEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AuditRecordMapperTest {

    private final AuditRecordMapper mapper = Mappers.getMapper(AuditRecordMapper.class);

    @Test
    void fromDecisionMadeEvent_shouldMapEventToAuditRecordEntity() {
        var evaluatedAt = Instant.parse("2025-01-01T12:00:00Z");
        var event = DecisionMadeEvent.builder()
            .transactionId("tx-1")
            .customerId("customer_1")
            .politicallyExposedPerson(false)
            .amount(BigDecimal.TEN)
            .country("PL")
            .ruleSetVersion("v1")
            .decisionId("dec_1")
            .decision(Decision.APPROVED.name())
            .evaluatedAt(evaluatedAt)
            .build();

        var entity = mapper.fromDecisionMadeEvent(event);

        assertThat(entity.id()).isNull();
        assertThat(entity.amount()).isEqualTo(BigDecimal.TEN);
        assertThat(entity.customerId()).isEqualTo("customer_1");
        assertThat(entity.country()).isEqualTo("PL");
        assertThat(entity.politicallyExposedPerson()).isFalse();
        assertThat(entity.transactionId()).isEqualTo("tx-1");
        assertThat(entity.decisionId()).isEqualTo("dec_1");
        assertThat(entity.ruleSetVersion()).isEqualTo("v1");
        assertThat(entity.decision()).isEqualTo(Decision.APPROVED.name());
        assertThat(entity.timestamp()).isEqualTo(evaluatedAt);
    }
}