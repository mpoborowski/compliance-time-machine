package com.aquacode.ctm.history.infrastructure.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record HistoricalReplayResponse(
    String transactionId,
    Instant historicalTimestamp,
    String ruleSetVersion,
    String decision,
    List<HistoricalRuleReplayResponse> rules,
    Instant replayedAt
) {
}