package com.aquacode.ctm.rules.application;

import com.aquacode.ctm.rules.RuleEvaluationContext;
import com.aquacode.ctm.rules.RuleOutcome;
import com.aquacode.ctm.rules.RuleSetNotFoundException;
import com.aquacode.ctm.rules.infrastructure.persistence.RuleSetEntity;
import com.aquacode.ctm.rules.infrastructure.persistence.RuleSetEntityMapper;
import com.aquacode.ctm.rules.infrastructure.persistence.RuleSetRepository;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.aquacode.ctm.rules.RuleEntityFixtures.amountThresholdDefinitionEntity;
import static com.aquacode.ctm.rules.RuleEntityFixtures.highRiskCountryRuleDefinitionEntity;
import static com.aquacode.ctm.rules.RuleEntityFixtures.pepRuleDefinitionEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SQLRuleSetResolverTest {

    private final RuleSetRepository ruleSetRepository = mock(RuleSetRepository.class);
    private final RuleSetEntityMapper mapper = Mappers.getMapper(RuleSetEntityMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final SQLRuleSetResolver resolver = new SQLRuleSetResolver(
        ruleSetRepository,
        mapper,
        objectMapper
    );

    @Test
    void resolve_shouldReturnRuleSetActiveAtPointInTime() {
        var pointInTime = Instant.parse("2025-07-01T00:00:00Z");
        var effectiveFrom = Instant.parse("2025-01-01T00:00:00Z");

        var entity = RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v1.0.0")
            .effectiveFrom(effectiveFrom)
            .effectiveTo(null)
            .rules(Set.of(
                pepRuleDefinitionEntity(30, true),
                highRiskCountryRuleDefinitionEntity(20, true, """
                    {"countries": ["RU", "KP"]}
                    """),
                amountThresholdDefinitionEntity(10, true, """
                    {"threshold": 100.00}
                    """)
            ))
            .build();

        when(ruleSetRepository.findActiveAt(pointInTime)).thenReturn(Optional.of(entity));

        var result = resolver.resolve(pointInTime);

        assertThat(result).isNotNull();
        assertThat(result.version()).isEqualTo("v1.0.0");
        assertThat(result.effectiveFrom()).isEqualTo(effectiveFrom);
        assertThat(result.rules()).hasSize(3);
    }

    @Test
    void resolve_shouldMaterializeRulesOrderedByPriority() {
        var pointInTime = Instant.parse("2025-07-01T00:00:00Z");

        var entity = RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v1.0.0")
            .effectiveFrom(Instant.parse("2025-01-01T00:00:00Z"))
            .effectiveTo(null)
            .rules(Set.of(
                pepRuleDefinitionEntity(30, true),
                highRiskCountryRuleDefinitionEntity(20, true, """
                    {"countries": ["RU", "KP"]}
                    """),
                amountThresholdDefinitionEntity(10, true, """
                    {"threshold": 100.00}
                    """)
            ))
            .build();

        when(ruleSetRepository.findActiveAt(pointInTime)).thenReturn(Optional.of(entity));

        var result = resolver.resolve(pointInTime);

        assertThat(result.rules())
            .extracting(rule -> rule.metadata().code())
            .containsExactly(
                "AMOUNT_THRESHOLD",
                "HIGH_RISK_COUNTRY",
                "PEP"
            );
    }

    @Test
    void resolve_shouldIgnoreDisabledRules() {
        var pointInTime = Instant.parse("2025-07-01T00:00:00Z");

        var entity = RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v1.0.0")
            .effectiveFrom(Instant.parse("2025-01-01T00:00:00Z"))
            .effectiveTo(null)
            .rules(Set.of(
                pepRuleDefinitionEntity(10, false),
                amountThresholdDefinitionEntity(20, true, """
                    {"threshold": 100.00}
                    """)
            ))
            .build();

        when(ruleSetRepository.findActiveAt(pointInTime)).thenReturn(Optional.of(entity));

        var result = resolver.resolve(pointInTime);

        assertThat(result.rules())
            .singleElement()
            .satisfies(rule -> assertThat(rule.metadata().code()).isEqualTo("AMOUNT_THRESHOLD"));
    }

    @Test
    void resolve_shouldMaterializeAmountThresholdRuleFromJsonConfiguration() {
        var pointInTime = Instant.parse("2025-07-01T00:00:00Z");

        var entity = RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v1.0.0")
            .effectiveFrom(Instant.parse("2025-01-01T00:00:00Z"))
            .effectiveTo(null)
            .rules(Set.of(
                amountThresholdDefinitionEntity(10, true, """
                    {"threshold": 100.00}
                    """)
            ))
            .build();

        when(ruleSetRepository.findActiveAt(pointInTime)).thenReturn(Optional.of(entity));

        var result = resolver.resolve(pointInTime);
        var rule = result.rules().getFirst();

        assertThat(rule.evaluate(new RuleEvaluationContext("PL", BigDecimal.valueOf(100), false)).outcome())
            .isEqualTo(RuleOutcome.PASS);

        assertThat(rule.evaluate(new RuleEvaluationContext("PL", BigDecimal.valueOf(101), false)).outcome())
            .isEqualTo(RuleOutcome.FAIL);
    }

    @Test
    void resolve_shouldMaterializeHighRiskCountryRuleFromJsonConfiguration() {
        var pointInTime = Instant.parse("2025-07-01T00:00:00Z");

        var entity = RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v1.0.0")
            .effectiveFrom(Instant.parse("2025-01-01T00:00:00Z"))
            .effectiveTo(null)
            .rules(Set.of(
                highRiskCountryRuleDefinitionEntity(10, true, """
                    {"countries": ["RU", "KP"]}
                    """)
            ))
            .build();

        when(ruleSetRepository.findActiveAt(pointInTime)).thenReturn(Optional.of(entity));

        var result = resolver.resolve(pointInTime);
        var rule = result.rules().getFirst();

        assertThat(rule.evaluate(new RuleEvaluationContext("PL", BigDecimal.TEN, false)).outcome())
            .isEqualTo(RuleOutcome.PASS);

        assertThat(rule.evaluate(new RuleEvaluationContext("RU", BigDecimal.TEN, false)).outcome())
            .isEqualTo(RuleOutcome.FAIL);
    }

    @Test
    void resolve_shouldMaterializePepRule() {
        var pointInTime = Instant.parse("2025-07-01T00:00:00Z");

        var entity = RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v1.0.0")
            .effectiveFrom(Instant.parse("2025-01-01T00:00:00Z"))
            .effectiveTo(null)
            .rules(Set.of(pepRuleDefinitionEntity(10, true)))
            .build();

        when(ruleSetRepository.findActiveAt(pointInTime)).thenReturn(Optional.of(entity));

        var result = resolver.resolve(pointInTime);
        var rule = result.rules().getFirst();

        assertThat(rule.evaluate(new RuleEvaluationContext("PL", BigDecimal.TEN, false)).outcome())
            .isEqualTo(RuleOutcome.PASS);

        assertThat(rule.evaluate(new RuleEvaluationContext("PL", BigDecimal.TEN, true)).outcome())
            .isEqualTo(RuleOutcome.FAIL);
    }

    @Test
    void resolve_shouldReturnRuleSetWithEmptyRulesWhenEntityRulesAreNull() {
        var pointInTime = Instant.parse("2025-07-01T00:00:00Z");

        var entity = RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v1.0.0")
            .effectiveFrom(Instant.parse("2025-01-01T00:00:00Z"))
            .effectiveTo(null)
            .rules(null)
            .build();

        when(ruleSetRepository.findActiveAt(pointInTime)).thenReturn(Optional.of(entity));

        var result = resolver.resolve(pointInTime);

        assertThat(result.rules()).isEmpty();
    }

    @Test
    void resolve_shouldThrowRuleSetNotFoundExceptionWhenNoActiveRuleSetExists() {
        var pointInTime = Instant.parse("2024-01-01T00:00:00Z");

        when(ruleSetRepository.findActiveAt(pointInTime)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.resolve(pointInTime))
            .isInstanceOf(RuleSetNotFoundException.class)
            .hasMessageContaining("No active rule set found for point in time: 2024-01-01T00:00:00Z");
    }

    @Test
    void resolve_shouldThrowExceptionWhenAmountThresholdConfigurationIsMissingThreshold() {
        var pointInTime = Instant.parse("2025-07-01T00:00:00Z");

        var entity = RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v1.0.0")
            .effectiveFrom(Instant.parse("2025-01-01T00:00:00Z"))
            .effectiveTo(null)
            .rules(Set.of(
                amountThresholdDefinitionEntity(10, true, "{}")
            ))
            .build();

        when(ruleSetRepository.findActiveAt(pointInTime)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> resolver.resolve(pointInTime))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("requires numeric JSON configuration field 'threshold'");
    }

    @Test
    void resolve_shouldThrowExceptionWhenHighRiskCountryConfigurationIsMissingCountries() {
        var pointInTime = Instant.parse("2025-07-01T00:00:00Z");

        var entity = RuleSetEntity.builder()
            .id(UUID.randomUUID())
            .version("v1.0.0")
            .effectiveFrom(Instant.parse("2025-01-01T00:00:00Z"))
            .effectiveTo(null)
            .rules(Set.of(
                highRiskCountryRuleDefinitionEntity(10, true, "{}")
            ))
            .build();

        when(ruleSetRepository.findActiveAt(pointInTime)).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> resolver.resolve(pointInTime))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("requires array JSON configuration field 'countries'");
    }
}