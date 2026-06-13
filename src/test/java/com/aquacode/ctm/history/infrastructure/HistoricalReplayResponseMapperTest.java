package com.aquacode.ctm.history.infrastructure;

import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.history.application.HistoricalDecisionReplay;
import com.aquacode.ctm.history.application.HistoricalRuleReplay;
import com.aquacode.ctm.rules.RuleOutcome;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HistoricalReplayResponseMapperTest {

    private final HistoricalReplayResponseMapper mapper = Mappers.getMapper(HistoricalReplayResponseMapper.class);

    @Test
    void toResponse_shouldMapHistoricalDecisionReplayToResponse() {
        var replay = new HistoricalDecisionReplay(
            "TX-123",
            Instant.parse("2025-03-01T10:00:00Z"),
            "2025-Q1",
            Decision.APPROVED,
            List.of(
                new HistoricalRuleReplay(
                    "AML-001",
                    "v1",
                    RuleOutcome.PASS,
                    "Transaction amount below threshold"
                )
            ),
            Instant.parse("2026-06-13T15:40:00Z")
        );

        var response = mapper.toResponse(replay);

        assertThat(response.transactionId()).isEqualTo("TX-123");
        assertThat(response.historicalTimestamp()).isEqualTo(Instant.parse("2025-03-01T10:00:00Z"));
        assertThat(response.ruleSetVersion()).isEqualTo("2025-Q1");
        assertThat(response.decision()).isEqualTo("APPROVED");
        assertThat(response.replayedAt()).isEqualTo(Instant.parse("2026-06-13T15:40:00Z"));

        assertThat(response.rules()).hasSize(1);
        assertThat(response.rules().getFirst().ruleCode()).isEqualTo("AML-001");
        assertThat(response.rules().getFirst().version()).isEqualTo("v1");
        assertThat(response.rules().getFirst().outcome()).isEqualTo("PASS");
        assertThat(response.rules().getFirst().explanation()).isEqualTo("Transaction amount below threshold");
    }
}