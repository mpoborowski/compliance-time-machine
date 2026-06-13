package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.audit.infrastructure.persistence.AuditRepository;
import com.aquacode.ctm.shared.DecisionMadeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditRepository auditRepository;
    private final AuditRecordMapper mapper;

    @ApplicationModuleListener
    public void onEvent(DecisionMadeEvent event) {
        auditRepository.save(mapper.fromDecisionMadeEvent(event));
    }
}