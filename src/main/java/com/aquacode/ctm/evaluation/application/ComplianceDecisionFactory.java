package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.ComplianceDecision;
import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.rules.RuleResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

final class ComplianceDecisionFactory {

    static ComplianceDecision create(List<RuleResult> results, String ruleSetVersion) {
        var rejected = results.stream().anyMatch(RuleResult::failed);
        var decision = rejected ? Decision.REJECTED : Decision.APPROVED;

        return new ComplianceDecision(
            UUID.randomUUID().toString(),
            decision,
            ruleSetVersion,
            Instant.now(),
            results
        );
    }
}
