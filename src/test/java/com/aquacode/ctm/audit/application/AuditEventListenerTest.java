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
        return new DecisionMadeEvent(
            "tx-1",
            "dec_1",
            Decision.APPROVED.name(),
            "v1",
            Instant.parse("2025-01-01T12:00:00Z")
        );
    }

    private static AuditRecordEntity givenAuditRecord() {
        return new AuditRecordEntity(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "tx-1",
            "dec_1",
            "v1",
            Decision.APPROVED.name(),
            Instant.parse("2025-01-01T12:00:00Z")
        );
    }
}