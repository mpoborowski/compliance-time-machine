package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.audit.infrastructure.persistence.AuditRecordEntity;
import com.aquacode.ctm.audit.infrastructure.persistence.AuditRepository;
import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.shared.DecisionMadeEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditEventListenerTest {

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private AuditRecordMapper auditRecordMapper;

    @InjectMocks
    private AuditEventListener auditEventListener;

    @Test
    void onEvent_shouldPersistAuditRecordMappedFromDecisionMadeEvent() {
        var event = givenDecisionMadeEvent();
        var auditRecord = givenAuditRecord();

        when(auditRecordMapper.fromDecisionMadeEvent(event))
            .thenReturn(auditRecord);

        auditEventListener.onEvent(event);

        verify(auditRecordMapper).fromDecisionMadeEvent(event);
        verify(auditRepository).save(auditRecord);
    }

    private static DecisionMadeEvent givenDecisionMadeEvent() {
        return DecisionMadeEvent.builder()
            .transactionId("tx-1")
            .customerId("customer-1")
            .politicallyExposedPerson(false)
            .amount(BigDecimal.TEN)
            .country("PL")
            .ruleSetVersion("v1")
            .decisionId("dec_1")
            .decision(Decision.APPROVED.name())
            .evaluatedAt(Instant.parse("2025-01-01T12:00:00Z"))
            .build();
    }

    private static AuditRecordEntity givenAuditRecord() {
        return AuditRecordEntity.builder()
            .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
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
    }
}