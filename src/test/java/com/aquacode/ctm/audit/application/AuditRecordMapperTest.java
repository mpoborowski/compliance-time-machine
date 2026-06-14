package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.evaluation.Decision;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static com.aquacode.ctm.shared.DecisionMadeFixtures.decisionMadeEvent;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuditRecordMapperTest {

    private final AuditRecordMapper mapper = Mappers.getMapper(AuditRecordMapper.class);

    @Test
    void fromDecisionMadeEvent_shouldMapEventToAuditRecordEntity() {
        var event = decisionMadeEvent();

        var entity = mapper.fromDecisionMadeEvent(event);

        assertAll(
            () -> assertThat(entity.id()).isNull(),
            () -> assertThat(entity.amount()).isEqualTo(BigDecimal.TEN),
            () -> assertThat(entity.customerId()).isEqualTo("customer-1"),
            () -> assertThat(entity.country()).isEqualTo("PL"),
            () -> assertThat(entity.politicallyExposedPerson()).isFalse(),
            () -> assertThat(entity.transactionId()).isEqualTo("tx-1"),
            () -> assertThat(entity.decisionId()).isEqualTo("dec_1"),
            () -> assertThat(entity.ruleSetVersion()).isEqualTo("v1"),
            () -> assertThat(entity.decision()).isEqualTo(Decision.APPROVED.name()),
            () -> assertThat(entity.timestamp()).isEqualTo(Instant.parse("2025-01-01T12:00:00Z"))
        );
    }
}