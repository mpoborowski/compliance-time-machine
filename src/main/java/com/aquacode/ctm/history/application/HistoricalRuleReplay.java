package com.aquacode.ctm.history.application;

import com.aquacode.ctm.rules.RuleOutcome;
import lombok.Builder;

@Builder
public record HistoricalRuleReplay(
    String ruleCode,
    String version,
    RuleOutcome outcome,
    String explanation
) {
}