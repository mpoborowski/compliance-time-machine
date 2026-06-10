package com.aquacode.ctm.rules;

import com.aquacode.ctm.rules.application.AmountThresholdRule;
import com.aquacode.ctm.rules.application.HighRiskCountryRule;
import com.aquacode.ctm.rules.application.PepRule;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public final class RulesTestDataProvider {

    public static final AmountThresholdRule AMOUNT_THRESHOLD_RULE =
        new AmountThresholdRule(
            BigDecimal.TEN,
            new RuleMetadata("AML-003", "v1", "Amount threshold check"));

    public static final HighRiskCountryRule HIGH_RISK_COUNTRY_RULE =
        new HighRiskCountryRule(
            Set.of("RU", "KP", "IR", "MM"),
            new RuleMetadata("AML-002", "v1", "High risk country check"));

    public static final PepRule PEP_RULE =
        new PepRule(
            new RuleMetadata("AML-001", "v1", "PEP check"));

    public static final RuleSet RULE_SET =
        new RuleSet(
            "v1",
            Instant.now(),
            List.of(PEP_RULE, HIGH_RISK_COUNTRY_RULE, AMOUNT_THRESHOLD_RULE)
        );

    public static RuleEvaluationContext context(BigDecimal amount, String country, boolean pep) {
        return new RuleEvaluationContext(country, amount, pep);
    }
}
