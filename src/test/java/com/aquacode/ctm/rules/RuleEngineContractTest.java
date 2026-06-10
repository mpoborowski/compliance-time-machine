package com.aquacode.ctm.rules;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.aquacode.ctm.rules.RulesTestDataProvider.RULE_SET;
import static com.aquacode.ctm.rules.RulesTestDataProvider.context;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

abstract class RuleEngineContractTest {
    protected abstract RuleEngine ruleEngine();

    @Test
    void evaluate_allRulesShouldPass() {
        var results = ruleEngine().evaluate(RULE_SET, context(BigDecimal.ONE, "PL", false));

        assertThat(results).allMatch(r -> r.outcome() == RuleOutcome.PASS);
    }

    @Test
    void evaluate_pepRuleShouldFail() {
        var results = ruleEngine().evaluate(RULE_SET, context(BigDecimal.ONE, "PL", true));

        assertThat(results)
            .filteredOn(r -> r.metadata().code().equals("AML-001"))
            .singleElement()
            .satisfies(r -> assertThat(r.outcome()).isEqualTo(RuleOutcome.FAIL));
    }

    @Test
    void evaluate_highRiskCountryRuleShouldFail() {
        var results = ruleEngine().evaluate(RULE_SET, context(BigDecimal.ONE, "RU", false));

        assertThat(results)
            .filteredOn(r -> r.metadata().code().equals("AML-002"))
            .singleElement()
            .extracting(RuleResult::outcome)
            .isEqualTo(RuleOutcome.FAIL);
    }

    @Test
    void evaluate_shouldFailOnAmountExceedingThreshold() {
        var results = ruleEngine().evaluate(RULE_SET, context(BigDecimal.valueOf(100), "PL", false));

        assertThat(results)
            .filteredOn(r -> r.metadata().code().equals("AML-003"))
            .singleElement()
            .extracting(RuleResult::outcome)
            .isEqualTo(RuleOutcome.FAIL);
    }

    @Test
    void evaluate_allRulesShouldFail() {
        var results = ruleEngine().evaluate(RULE_SET, context(BigDecimal.valueOf(100), "RU", true));

        assertThat(results).allMatch(r -> r.outcome() == RuleOutcome.FAIL);
    }

    @Test
    void evaluate_shouldPassOnExactAmountAsThreshold() {
        var results = ruleEngine().evaluate(RULE_SET, context(BigDecimal.TEN, "PL", false));

        assertThat(results)
            .filteredOn(r -> r.metadata().code().equals("AML-003"))
            .singleElement()
            .extracting(RuleResult::outcome)
            .isEqualTo(RuleOutcome.PASS);
    }

    @Test
    void evaluate_shouldReturnEmptyResultsForEmptyRuleset() {
        var ruleSet = new RuleSet("v1", Instant.now(), List.of());

        var results = ruleEngine().evaluate(ruleSet, context(BigDecimal.ONE, "PL", false));

        assertThat(results).isEmpty();
    }
}
