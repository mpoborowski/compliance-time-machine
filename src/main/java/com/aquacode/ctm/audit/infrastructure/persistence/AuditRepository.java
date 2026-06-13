package com.aquacode.ctm.audit.infrastructure.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface AuditRepository extends CrudRepository<AuditRecordEntity, UUID> {
}