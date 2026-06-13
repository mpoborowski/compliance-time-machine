package com.aquacode.ctm.rules.application;

import com.aquacode.ctm.rules.Rule;
import com.aquacode.ctm.rules.RuleMetadata;
import com.aquacode.ctm.rules.RuleSet;
import com.aquacode.ctm.rules.RuleSetResolver;
import com.aquacode.ctm.rules.infrastructure.persistence.RuleDefinitionEntity;
import com.aquacode.ctm.rules.infrastructure.persistence.RuleSetEntity;
import com.aquacode.ctm.rules.infrastructure.persistence.RuleSetEntityMapper;
import com.aquacode.ctm.rules.infrastructure.persistence.RuleSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class SQLRuleSetResolver implements RuleSetResolver {

    private final RuleSetRepository ruleSetRepository;
    private final RuleSetEntityMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    public RuleSet resolve(Instant pointInTime) {
        return ruleSetRepository.findActiveAt(pointInTime)
            .map(this::toDomain)
            .orElseThrow(() -> new RuleSetNotFoundException(pointInTime));
    }

    private RuleSet toDomain(RuleSetEntity entity) {
        var rules = nonNullRuleDefinitions(entity.rules())
            .stream()
            .filter(rule -> Boolean.TRUE.equals(rule.enabled()))
            .sorted(Comparator.comparing(RuleDefinitionEntity::priority))
            .map(this::toRule)
            .toList();

        return mapper.toDomain(entity, rules);
    }

    private static Set<RuleDefinitionEntity> nonNullRuleDefinitions(Set<RuleDefinitionEntity> rules) {
        return rules == null ? Set.of() : rules;
    }

    private Rule toRule(RuleDefinitionEntity entity) {
        var metadata = new RuleMetadata(
            entity.code(),
            entity.version(),
            entity.description()
        );

        var configuration = readConfiguration(entity);

        return switch (entity.type()) {
            case AMOUNT_THRESHOLD -> new AmountThresholdRule(
                requiredDecimal(configuration, "threshold", entity),
                metadata
            );
            case HIGH_RISK_COUNTRY -> new HighRiskCountryRule(
                requiredStringSet(configuration, "countries", entity),
                metadata
            );
            case PEP -> new PepRule(metadata);
        };
    }

    private JsonNode readConfiguration(RuleDefinitionEntity entity) {
        return objectMapper.readTree(entity.configuration() == null ? "{}" : entity.configuration());
    }

    private BigDecimal requiredDecimal(JsonNode configuration, String fieldName, RuleDefinitionEntity entity) {
        var value = configuration.get(fieldName);

        if (value == null || value.isNull() || !value.isNumber()) {
            throw new IllegalArgumentException(
                "Rule %s:%s requires numeric JSON configuration field '%s'"
                    .formatted(entity.code(), entity.version(), fieldName)
            );
        }

        return value.decimalValue();
    }

    private Set<String> requiredStringSet(JsonNode configuration, String fieldName, RuleDefinitionEntity entity) {
        var value = configuration.get(fieldName);

        if (value == null || !value.isArray()) {
            throw new IllegalArgumentException(
                "Rule %s:%s requires array JSON configuration field '%s'"
                    .formatted(entity.code(), entity.version(), fieldName)
            );
        }

        return StreamSupport.stream(value.spliterator(), false)
            .filter(JsonNode::isString)
            .map(JsonNode::asString)
            .collect(Collectors.toUnmodifiableSet());
    }
}