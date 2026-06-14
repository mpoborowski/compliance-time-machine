package com.aquacode.ctm.rules.application;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.aquacode.ctm.rules.RuleFixtures.evaluationContext;
import static com.aquacode.ctm.rules.RuleFixtures.pepRule;
import static org.assertj.core.api.Assertions.assertThat;

class PepRuleTest {

    @Test
    void evaluate_shouldPassOnNonPepCustomer() {
        var result = pepRule().evaluate(evaluationContext(BigDecimal.TEN, "PL", false));

        assertThat(result).isEqualTo(pepRule().passedResult());
    }

    @Test
    void evaluate_shouldFailOnPepCustomer() {
        var result = pepRule().evaluate(evaluationContext(BigDecimal.TEN, "PL", true));

        assertThat(result).isEqualTo(pepRule().failedResult(PepRule.FAILED_EXPLANATION));
    }

}