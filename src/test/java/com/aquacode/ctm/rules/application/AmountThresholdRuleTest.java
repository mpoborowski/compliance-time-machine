package com.aquacode.ctm.rules.application;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.aquacode.ctm.rules.RulesTestDataProvider.AMOUNT_THRESHOLD_RULE;
import static com.aquacode.ctm.rules.RulesTestDataProvider.context;
import static org.assertj.core.api.Assertions.assertThat;

class AmountThresholdRuleTest {

    @Test
    void evaluate_shouldPassOnAmountLowerThanThreshold() {
        var result = AMOUNT_THRESHOLD_RULE.evaluate(context(BigDecimal.valueOf(9), "PL", false));

        assertThat(result).isEqualTo(AMOUNT_THRESHOLD_RULE.passedResult());
    }

    @Test
    void evaluate_shouldPassOnExactAmountAsThreshold() {
        var result = AMOUNT_THRESHOLD_RULE.evaluate(context(BigDecimal.TEN, "PL", false));

        assertThat(result).isEqualTo(AMOUNT_THRESHOLD_RULE.passedResult());
    }

    @Test
    void evaluate_shouldFailOnAmountExceedingThreshold() {
        var exceededAmount = BigDecimal.valueOf(11);
        var result = AMOUNT_THRESHOLD_RULE.evaluate(context(exceededAmount, "PL", false));

        assertThat(result).isEqualTo(AMOUNT_THRESHOLD_RULE.failedResult(AmountThresholdRule.FAILED_EXPLANATION.formatted(exceededAmount, BigDecimal.TEN)));
    }
}