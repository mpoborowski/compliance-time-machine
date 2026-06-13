package com.aquacode.ctm.audit.infrastructure.persistence;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Builder
@Table("audit_record_entity")
public record AuditRecordEntity(
    @Id
    UUID id,
    String transactionId,
    String decisionId,
    String ruleSetVersion,
    String decision,
    Instant timestamp
) {
}