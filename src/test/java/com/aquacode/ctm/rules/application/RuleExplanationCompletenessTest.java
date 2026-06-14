package com.aquacode.ctm.rules.application;

import com.aquacode.ctm.rules.RuleResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.aquacode.ctm.rules.RuleFixtures.amountThresholdRule;
import static com.aquacode.ctm.rules.RuleFixtures.evaluationContext;
import static com.aquacode.ctm.rules.RuleFixtures.highRiskCountryRule;
import static com.aquacode.ctm.rules.RuleFixtures.pepRule;
import static org.assertj.core.api.Assertions.assertThat;

class RuleExplanationCompletenessTest {

    @Test
    void realRules_shouldReturnExplanationForPassAndFailResults() {
        assertExplanationPresent(amountThresholdRule().evaluate(evaluationContext(BigDecimal.valueOf(9), "PL", false)));
        assertExplanationPresent(amountThresholdRule().evaluate(evaluationContext(BigDecimal.valueOf(11), "PL", false)));

        assertExplanationPresent(highRiskCountryRule().evaluate(evaluationContext(BigDecimal.TEN, "PL", false)));
        assertExplanationPresent(highRiskCountryRule().evaluate(evaluationContext(BigDecimal.TEN, "RU", false)));

        assertExplanationPresent(pepRule().evaluate(evaluationContext(BigDecimal.TEN, "PL", false)));
        assertExplanationPresent(pepRule().evaluate(evaluationContext(BigDecimal.TEN, "PL", true)));
    }

    private static void assertExplanationPresent(RuleResult result) {
        assertThat(result.explanation()).isNotBlank();
    }
}