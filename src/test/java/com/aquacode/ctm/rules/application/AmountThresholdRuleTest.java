package com.aquacode.ctm.rules.application;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.aquacode.ctm.rules.RuleFixtures.amountThresholdRule;
import static com.aquacode.ctm.rules.RuleFixtures.evaluationContext;
import static org.assertj.core.api.Assertions.assertThat;

class AmountThresholdRuleTest {

    @Test
    void evaluate_shouldPassOnAmountLowerThanThreshold() {
        var result = amountThresholdRule().evaluate(evaluationContext(BigDecimal.valueOf(9), "PL", false));

        assertThat(result).isEqualTo(amountThresholdRule().passedResult());
    }

    @Test
    void evaluate_shouldPassOnExactAmountAsThreshold() {
        var result = amountThresholdRule().evaluate(evaluationContext(BigDecimal.TEN, "PL", false));

        assertThat(result).isEqualTo(amountThresholdRule().passedResult());
    }

    @Test
    void evaluate_shouldFailOnAmountExceedingThreshold() {
        var exceededAmount = BigDecimal.valueOf(11);
        var result = amountThresholdRule().evaluate(evaluationContext(exceededAmount, "PL", false));

        assertThat(result).isEqualTo(amountThresholdRule().failedResult(AmountThresholdRule.FAILED_EXPLANATION.formatted(exceededAmount, BigDecimal.TEN)));
    }
}