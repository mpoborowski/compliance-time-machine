package com.aquacode.ctm.evaluation.infrastructure.dto;

import java.time.Instant;
import java.util.List;

public record EvaluateTransactionResponse(String decisionId,
                                          String decision,
                                          String appliedRuleSetVersion,
                                          Instant evaluatedAt,
                                          List<TriggeredRuleResponse> triggeredRules) {
}
