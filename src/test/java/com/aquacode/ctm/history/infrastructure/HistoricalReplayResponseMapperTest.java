package com.aquacode.ctm.history.infrastructure;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.Objects;

import static com.aquacode.ctm.history.HistoryFixtures.historicalDecisionReplay;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class HistoricalReplayResponseMapperTest {

    private final HistoricalReplayResponseMapper mapper = Mappers.getMapper(HistoricalReplayResponseMapper.class);

    @Test
    void toResponse_shouldMapHistoricalDecisionReplayToResponse() {
        var replay = historicalDecisionReplay();

        var response = mapper.toResponse(replay);

        assertAll(
            () -> assertThat(response.transactionId()).isEqualTo("tx-1"),
            () -> assertThat(response.historicalTimestamp()).isEqualTo(Instant.parse("2025-03-01T10:00:00Z")),
            () -> assertThat(response.ruleSetVersion()).isEqualTo("v1"),
            () -> assertThat(response.decision()).isEqualTo("APPROVED"),
            () -> assertThat(response.replayedAt()).isEqualTo(Instant.parse("2026-06-13T15:40:00Z"))
        );

        assertAll(
            () -> assertThat(response.rules()).hasSize(1),
            () -> assertThat(Objects.requireNonNull(response.rules()).getFirst().ruleCode()).isEqualTo("AML-001"),
            () -> assertThat(Objects.requireNonNull(response.rules()).getFirst().version()).isEqualTo("v1"),
            () -> assertThat(Objects.requireNonNull(response.rules()).getFirst().outcome()).isEqualTo("PASS"),
            () -> assertThat(Objects.requireNonNull(response.rules()).getFirst().explanation()).isEqualTo("Transaction amount below threshold")
        );
    }
}