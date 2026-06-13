package com.aquacode.ctm.rules.infrastructure.persistence;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RuleSetRepository extends CrudRepository<RuleSetEntity, UUID> {

    @Query("""
        SELECT *
        FROM rule_set_entity
        WHERE effective_from <= :pointInTime
          AND (effective_to IS NULL OR effective_to > :pointInTime)
        ORDER BY effective_from DESC
        LIMIT 1
        """)
    Optional<RuleSetEntity> findActiveAt(Instant pointInTime);
}