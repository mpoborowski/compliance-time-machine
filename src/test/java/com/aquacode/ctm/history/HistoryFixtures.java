package com.aquacode.ctm.history;

import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.history.application.HistoricalDecisionReplay;
import com.aquacode.ctm.history.application.HistoricalRuleReplay;
import com.aquacode.ctm.rules.RuleOutcome;

import java.time.Instant;
import java.util.List;

public final class HistoryFixtures {

    public static HistoricalDecisionReplay historicalDecisionReplay() {
        return HistoricalDecisionReplay.builder()
            .transactionId("tx-1")
            .historicalTimestamp(Instant.parse("2025-03-01T10:00:00Z"))
            .ruleSetVersion("v1")
            .decision(Decision.APPROVED)
            .replayedAt(Instant.parse("2026-06-13T15:40:00Z"))
            .rules(List.of(historicalRuleReplay()))
            .build();
    }

    public static HistoricalRuleReplay historicalRuleReplay() {
        return HistoricalRuleReplay.builder()
            .ruleCode("AML-001")
            .version("v1")
            .outcome(RuleOutcome.PASS)
            .explanation("Transaction amount below threshold")
            .build();
    }
}
