package com.aquacode.ctm.evaluation;

import com.aquacode.ctm.rules.RuleResult;

import java.time.Instant;
import java.util.Set;

public record ComplianceDecision(
    String decisionId,
    Decision decision,
    String ruleSetVersion,
    Instant evaluatedAt,
    Set<RuleResult> results
) {
}
