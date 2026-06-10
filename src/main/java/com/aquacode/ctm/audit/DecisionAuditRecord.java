package com.aquacode.ctm.audit;

import com.aquacode.ctm.evaluation.Decision;

import java.time.Instant;

public record DecisionAuditRecord(
    String transactionId,
    String decisionId,
    String ruleSetVersion,
    Decision decision,
    Instant timestamp
) {
}
