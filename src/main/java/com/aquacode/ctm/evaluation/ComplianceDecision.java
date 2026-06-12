package com.aquacode.ctm.evaluation;

import com.aquacode.ctm.rules.RuleResult;

import java.time.Instant;
import java.util.List;

public record ComplianceDecision(String decisionId,
                                 Decision decision,
                                 String ruleSetVersion,
                                 Instant evaluatedAt,
                                 List<RuleResult> results) {
}
