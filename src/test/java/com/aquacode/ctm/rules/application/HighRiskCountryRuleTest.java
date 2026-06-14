package com.aquacode.ctm.rules.application;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.aquacode.ctm.rules.RuleFixtures.evaluationContext;
import static com.aquacode.ctm.rules.RuleFixtures.highRiskCountryRule;
import static org.assertj.core.api.Assertions.assertThat;

class HighRiskCountryRuleTest {

    @Test
    void evaluate_shouldPassOnNonSanctionCountry() {
        var result = highRiskCountryRule().evaluate(evaluationContext(BigDecimal.TEN, "PL", false));

        assertThat(result).isEqualTo(highRiskCountryRule()
            .passedResult(HighRiskCountryRule.PASSED_EXPLANATION.formatted("PL")));
    }

    @Test
    void evaluate_shouldFailOnSanctionedCountry() {
        var sanctionedCountryCode = "RU";
        var result = highRiskCountryRule().evaluate(evaluationContext(BigDecimal.TEN, sanctionedCountryCode, false));

        assertThat(result).isEqualTo(highRiskCountryRule()
            .failedResult(HighRiskCountryRule.FAILED_EXPLANATION.formatted(sanctionedCountryCode)));
    }
}