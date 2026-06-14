package com.aquacode.ctm.evaluation;

import com.aquacode.ctm.rules.RuleResult;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record ComplianceDecision(String decisionId,
                                 Decision decision,
                                 String ruleSetVersion,
                                 Instant evaluatedAt,
                                 List<RuleResult> results
) {
}
