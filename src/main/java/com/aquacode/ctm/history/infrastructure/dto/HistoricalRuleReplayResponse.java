package com.aquacode.ctm.history.infrastructure.dto;

import lombok.Builder;

@Builder
public record HistoricalRuleReplayResponse(
    String ruleCode,
    String version,
    String outcome,
    String explanation
) {
}