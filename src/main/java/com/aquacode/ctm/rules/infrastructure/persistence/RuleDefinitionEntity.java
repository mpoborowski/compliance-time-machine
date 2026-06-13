package com.aquacode.ctm.rules.infrastructure.persistence;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Builder
@Table("rule_definition_entity")
public record RuleDefinitionEntity(
    @Id
    UUID id,
    String code,
    String version,
    String description,
    RuleDefinitionType type,
    Integer priority,
    Boolean enabled,
    String configuration) {
}