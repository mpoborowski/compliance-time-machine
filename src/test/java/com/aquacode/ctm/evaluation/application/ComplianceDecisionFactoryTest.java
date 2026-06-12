package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.rules.RuleOutcome;
import com.aquacode.ctm.rules.RuleResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComplianceDecisionFactoryTest {

    @Test
    void create_shouldReturnApprovedDecisionWhenAllRulesPassed() {
        var results = List.of(
            new RuleResult(null, RuleOutcome.PASS, null),
            new RuleResult(null, RuleOutcome.PASS, null)
        );

        var decision = ComplianceDecisionFactory.create(results, "v1");

        assertThat(decision.decision()).isEqualTo(Decision.APPROVED);
        assertThat(decision.ruleSetVersion()).isEqualTo("v1");
        assertThat(decision.results()).isEqualTo(results);
        assertThat(decision.decisionId()).isNotBlank();
        assertThat(decision.evaluatedAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void create_shouldReturnRejectedDecisionWhenAnyRuleFailed() {
        var results = List.of(
            new RuleResult(null, RuleOutcome.PASS, null),
            new RuleResult(null, RuleOutcome.FAIL, "failure")
        );

        var decision = ComplianceDecisionFactory.create(results, "v1");

        assertThat(decision.decision()).isEqualTo(Decision.REJECTED);
    }

}