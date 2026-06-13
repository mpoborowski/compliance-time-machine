package com.aquacode.ctm.shared;

import java.time.Instant;

public record DecisionMadeEvent(
    String transactionId,
    String decisionId,
    String decision,
    String ruleSetVersion,
    Instant evaluatedAt
) {
}