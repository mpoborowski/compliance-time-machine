package com.aquacode.ctm.rules.infrastructure.persistence;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Builder
@Table("rule_set_entity")
public record RuleSetEntity(
    @Id
    UUID id,
    String version,
    Instant effectiveFrom,
    Instant effectiveTo,

    @MappedCollection(idColumn = "rule_set_id")
    Set<RuleDefinitionEntity> rules) {
}