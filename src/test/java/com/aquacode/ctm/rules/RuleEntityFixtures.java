package com.aquacode.ctm.rules;

import com.aquacode.ctm.rules.infrastructure.persistence.RuleDefinitionEntity;
import com.aquacode.ctm.rules.infrastructure.persistence.RuleDefinitionType;
import com.aquacode.ctm.rules.infrastructure.persistence.RuleSetEntity;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public final class RuleEntityFixtures {

    public static RuleDefinitionEntity pepRuleDefinitionEntity(Integer priority, Boolean enabled) {
        return RuleDefinitionEntity.builder()
            .id(UUID.randomUUID())
            .code("PEP")
            .version("v1")
            .description("PEP rule")
            .type(RuleDefinitionType.PEP)
            .priority(priority)
            .enabled(enabled)
            .configuration("{}")
            .build();
    }

    public static RuleDefinitionEntity amountThresholdDefinitionEntity(Integer priority, Boolean enabled, String configuration) {
        return RuleDefinitionEntity.builder()
            .id(UUID.randomUUID())
            .code("AMOUNT_THRESHOLD")
            .version("v1")
            .description("Amount threshold rule")
            .type(RuleDefinitionType.AMOUNT_THRESHOLD)
            .priority(priority)
            .enabled(enabled)
            .configuration(configuration)
            .build();
    }

    public static RuleDefinitionEntity highRiskCountryRuleDefinitionEntity(Integer priority, Boolean enabled, String configuration) {
        return RuleDefinitionEntity.builder()
            .id(UUID.randomUUID())
            .code("HIGH_RISK_COUNTRY")
            .version("v1")
            .description("High risk country rule")
            .type(RuleDefinitionType.HIGH_RISK_COUNTRY)
            .priority(priority)
            .enabled(enabled)
            .configuration(configuration)
            .build();
    }

    public static RuleSetEntity ruleSetEntity() {
        return RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v2.0.0")
            .effectiveFrom(Instant.parse("2025-06-01T00:00:00Z"))
            .effectiveTo(null)
            .rules(Set.of(pepRuleDefinitionEntity(10, true)))
            .build();
    }

    public static RuleSetEntity ruleSetWithoutRules(Instant effectiveFrom, Instant effectiveTo) {
        return RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v1.0.0")
            .effectiveFrom(effectiveFrom)
            .effectiveTo(effectiveTo)
            .rules(Set.of())
            .build();
    }
}
