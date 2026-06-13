package com.aquacode.ctm.history.application;

import com.aquacode.ctm.evaluation.Decision;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record HistoricalDecisionReplay(
    String transactionId,
    Instant historicalTimestamp,
    String ruleSetVersion,
    Decision decision,
    List<HistoricalRuleReplay> rules,
    Instant replayedAt
) {
}