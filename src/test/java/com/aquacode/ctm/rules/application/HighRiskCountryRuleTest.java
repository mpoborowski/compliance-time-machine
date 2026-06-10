package com.aquacode.ctm.rules.application;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.aquacode.ctm.rules.RulesTestDataProvider.HIGH_RISK_COUNTRY_RULE;
import static com.aquacode.ctm.rules.RulesTestDataProvider.context;
import static org.assertj.core.api.Assertions.assertThat;

class HighRiskCountryRuleTest {

    @Test
    void evaluate_shouldPassOnNonSanctionCountry() {
        var result = HIGH_RISK_COUNTRY_RULE.evaluate(context(BigDecimal.TEN, "PL", false));

        assertThat(result).isEqualTo(HIGH_RISK_COUNTRY_RULE.passedResult());
    }

    @Test
    void evaluate_shouldFailOnSanctionedCountry() {
        var sanctionedCountryCode = "RU";
        var result = HIGH_RISK_COUNTRY_RULE.evaluate(context(BigDecimal.TEN, sanctionedCountryCode, false));

        assertThat(result).isEqualTo(HIGH_RISK_COUNTRY_RULE.failedResult(HighRiskCountryRule.FAILED_EXPLANATION.formatted(sanctionedCountryCode)));
    }
}