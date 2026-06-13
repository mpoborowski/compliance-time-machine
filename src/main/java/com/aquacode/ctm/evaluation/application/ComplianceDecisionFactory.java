package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.ComplianceDecision;
import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.rules.RuleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
final class ComplianceDecisionFactory {

    private final ComplianceDecisionIdGenerator decisionIdGenerator;
    private final Clock clock;

    ComplianceDecision create(List<RuleResult> results, String ruleSetVersion) {
        var rejected = results.stream().anyMatch(RuleResult::failed);
        var decision = rejected ? Decision.REJECTED : Decision.APPROVED;

        return new ComplianceDecision(
            decisionIdGenerator.generate(),
            decision,
            ruleSetVersion,
            Instant.now(clock),
            results
        );
    }
}