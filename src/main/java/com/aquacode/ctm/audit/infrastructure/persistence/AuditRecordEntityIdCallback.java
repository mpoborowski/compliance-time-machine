package com.aquacode.ctm.audit.infrastructure.persistence;

import org.springframework.data.relational.core.mapping.event.BeforeConvertCallback;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class AuditRecordEntityIdCallback implements BeforeConvertCallback<AuditRecordEntity> {

    @Override
    public AuditRecordEntity onBeforeConvert(AuditRecordEntity entity) {
        if (entity.id() != null) {
            return entity;
        }

        return new AuditRecordEntity(
            UUID.randomUUID(),
            entity.transactionId(),
            entity.decisionId(),
            entity.ruleSetVersion(),
            entity.decision(),
            entity.timestamp()
        );
    }
}