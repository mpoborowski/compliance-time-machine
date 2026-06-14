package com.aquacode.ctm.rules.infrastructure.persistence;

import com.aquacode.ctm.rules.Rule;
import com.aquacode.ctm.rules.RuleEvaluationContext;
import com.aquacode.ctm.rules.RuleMetadata;
import com.aquacode.ctm.rules.RuleResult;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static com.aquacode.ctm.rules.RuleEntityFixtures.ruleSetEntity;
import static com.aquacode.ctm.rules.RuleEntityFixtures.ruleSetWithoutRules;
import static org.assertj.core.api.Assertions.assertThat;

class RuleSetEntityMapperTest {

    private final RuleSetEntityMapper mapper = Mappers.getMapper(RuleSetEntityMapper.class);

    @Test
    void toDomain_shouldMapRuleSetEntityToRuleSet() {
        var effectiveFrom = Instant.parse("2025-01-01T00:00:00Z");
        var effectiveTo = Instant.parse("2026-01-01T00:00:00Z");

        var entity = ruleSetWithoutRules(effectiveFrom, effectiveTo);

        var rules = List.of(testRule("AML-001"), testRule("AML-002"));

        var result = mapper.toDomain(entity, rules);

        assertThat(result).isNotNull();
        assertThat(result.version()).isEqualTo("v1.0.0");
        assertThat(result.effectiveFrom()).isEqualTo(effectiveFrom);
        assertThat(result.rules()).containsExactlyElementsOf(rules);
    }

    @Test
    void toDomain_shouldUseProvidedDomainRules() {
        var entity = ruleSetEntity();

        var domainRule = testRule("DOMAIN-RULE");

        var result = mapper.toDomain(entity, List.of(domainRule));

        assertThat(result.rules())
            .singleElement()
            .satisfies(rule -> assertThat(rule.metadata().code()).isEqualTo("DOMAIN-RULE"));
    }

    @Test
    void toDomain_shouldReturnNullWhenEntityIsNull() {
        assertThat(mapper.toDomain(null, List.of())).isNull();
    }

    private static Rule testRule(String code) {
        return new Rule() {
            @Override
            public RuleResult evaluate(RuleEvaluationContext context) {
                return passedResult();
            }

            @Override
            public RuleMetadata metadata() {
                return new RuleMetadata(code, "v1", "Test rule");
            }
        };
    }
}