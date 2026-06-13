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

        return AuditRecordEntity.builder()
            .id(UUID.randomUUID())
            .transactionId(entity.transactionId())
            .amount(entity.amount())
            .customerId(entity.customerId())
            .timestamp(entity.timestamp())
            .country(entity.country())
            .transactionTimestamp(entity.transactionTimestamp())
            .politicallyExposedPerson(entity.politicallyExposedPerson())
            .decisionId(entity.decisionId())
            .decision(entity.decision())
            .ruleSetVersion(entity.ruleSetVersion())
            .build();
    }
}