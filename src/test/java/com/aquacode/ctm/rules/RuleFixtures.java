package com.aquacode.ctm.rules;

import com.aquacode.ctm.rules.application.AmountThresholdRule;
import com.aquacode.ctm.rules.application.HighRiskCountryRule;
import com.aquacode.ctm.rules.application.PepRule;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public final class RuleFixtures {

    private static final AmountThresholdRule AMOUNT_THRESHOLD_RULE = new AmountThresholdRule(
            BigDecimal.TEN,
            new RuleMetadata("AML-003", "v1", "Amount threshold check"));

    private static final HighRiskCountryRule HIGH_RISK_COUNTRY_RULE = new HighRiskCountryRule(
            Set.of("RU", "KP", "IR", "MM"),
            new RuleMetadata("AML-002", "v1", "High risk country check"));

    private static final PepRule PEP_RULE = new PepRule(
            new RuleMetadata("AML-001", "v1", "PEP check"));

    public static RuleSet ruleSet() {
        return RuleSet.builder()
            .version("v1")
            .effectiveFrom(Instant.now())
            .rules(List.of(PEP_RULE, HIGH_RISK_COUNTRY_RULE, AMOUNT_THRESHOLD_RULE))
            .build();
    }

    public static AmountThresholdRule amountThresholdRule() {
        return AMOUNT_THRESHOLD_RULE;
    }

    public static HighRiskCountryRule highRiskCountryRule() {
        return HIGH_RISK_COUNTRY_RULE;
    }

    public static PepRule pepRule() {
        return PEP_RULE;
    }

    public static RuleResult passedResult() {
        return RuleResult.builder()
            .metadata(RuleMetadata.builder()
                .code("AML-001")
                .version("v1")
                .build())
            .outcome(RuleOutcome.PASS)
            .build();
    }

    public static RuleResult failedResult() {
        return RuleResult.builder()
            .metadata(RuleMetadata.builder()
                .code("AML-001")
                .version("v1")
                .build())
            .outcome(RuleOutcome.FAIL)
            .explanation("failure")
            .build();
    }

    public static RuleEvaluationContext evaluationContext(BigDecimal amount, String country, boolean pep) {
        return RuleEvaluationContext.builder()
            .country(country)
            .amount(amount)
            .politicallyExposedPerson(pep)
            .build();
    }
}
