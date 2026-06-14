package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.audit.infrastructure.persistence.AuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.aquacode.ctm.audit.AuditFixtures.auditRecordEntity;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.aquacode.ctm.shared.DecisionMadeFixtures.decisionMadeEvent;

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
        var event = decisionMadeEvent();
        var auditRecord = auditRecordEntity();

        when(auditRecordMapper.fromDecisionMadeEvent(event))
            .thenReturn(auditRecord);

        auditEventListener.onEvent(event);

        verify(auditRecordMapper).fromDecisionMadeEvent(event);
        verify(auditRepository).save(auditRecord);
    }
}