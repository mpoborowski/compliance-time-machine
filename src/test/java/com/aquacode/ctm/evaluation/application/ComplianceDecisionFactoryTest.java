package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.rules.RuleOutcome;
import com.aquacode.ctm.rules.RuleResult;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComplianceDecisionFactoryTest {

    private static final Instant NOW = Instant.parse("2025-01-01T12:00:00Z");

    private final ComplianceDecisionFactory factory = new ComplianceDecisionFactory(
        () -> "dec_test-id",
        Clock.fixed(NOW, ZoneOffset.UTC)
    );

    @Test
    void create_shouldReturnApprovedDecisionWhenAllRulesPassed() {
        var results = List.of(
            new RuleResult(null, RuleOutcome.PASS, null),
            new RuleResult(null, RuleOutcome.PASS, null)
        );

        var decision = factory.create(results, "v1");

        assertThat(decision.decision()).isEqualTo(Decision.APPROVED);
        assertThat(decision.ruleSetVersion()).isEqualTo("v1");
        assertThat(decision.results()).isEqualTo(results);
        assertThat(decision.decisionId()).isEqualTo("dec_test-id");
        assertThat(decision.evaluatedAt()).isEqualTo(NOW);
    }

    @Test
    void create_shouldReturnRejectedDecisionWhenAnyRuleFailed() {
        var results = List.of(
            new RuleResult(null, RuleOutcome.PASS, null),
            new RuleResult(null, RuleOutcome.FAIL, "failure")
        );

        var decision = factory.create(results, "v1");

        assertThat(decision.decision()).isEqualTo(Decision.REJECTED);
        assertThat(decision.decisionId()).isEqualTo("dec_test-id");
        assertThat(decision.evaluatedAt()).isEqualTo(NOW);
    }
}