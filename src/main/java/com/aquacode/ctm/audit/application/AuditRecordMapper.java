package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.audit.infrastructure.persistence.AuditRecordEntity;
import com.aquacode.ctm.shared.DecisionMadeEvent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditRecordMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "evaluatedAt", target = "timestamp")
    AuditRecordEntity fromDecisionMadeEvent(DecisionMadeEvent event);
}