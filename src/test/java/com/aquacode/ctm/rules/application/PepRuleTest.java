package com.aquacode.ctm.rules.application;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.aquacode.ctm.rules.RulesTestDataProvider.PEP_RULE;
import static com.aquacode.ctm.rules.RulesTestDataProvider.context;
import static org.assertj.core.api.Assertions.assertThat;

class PepRuleTest {

    @Test
    void evaluate_shouldPassOnNonPepCustomer() {
        var result = PEP_RULE.evaluate(context(BigDecimal.TEN, "PL", false));

        assertThat(result).isEqualTo(PEP_RULE.passedResult());
    }

    @Test
    void evaluate_shouldFailOnPepCustomer() {
        var result = PEP_RULE.evaluate(context(BigDecimal.TEN, "PL", true));

        assertThat(result).isEqualTo(PEP_RULE.failedResult(PepRule.FAILED_EXPLANATION));
    }

}