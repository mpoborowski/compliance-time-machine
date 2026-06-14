package com.aquacode.ctm.evaluation;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static com.aquacode.ctm.rules.RuleFixtures.failedResult;
import static com.aquacode.ctm.rules.RuleFixtures.passedResult;

public final class EvaluationFixtures {

    public static ComplianceDecision approvedDecision() {
        return ComplianceDecision.builder()
            .decisionId("dec_1")
            .decision(Decision.APPROVED)
            .ruleSetVersion("v1")
            .evaluatedAt(Clock.fixed(Instant.parse("2025-01-01T12:00:00Z"), ZoneOffset.UTC).instant())
            .results(List.of(passedResult()))
            .build();
    }

    public static ComplianceDecision failedDecision() {
        return ComplianceDecision.builder()
            .decisionId("dec_1")
            .decision(Decision.REJECTED)
            .ruleSetVersion("v1")
            .evaluatedAt(Clock.fixed(Instant.parse("2025-01-01T12:00:00Z"), ZoneOffset.UTC).instant())
            .results(List.of(failedResult()))
            .build();
    }
}
