package com.aquacode.ctm.history.infrastructure;

import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.history.application.HistoricalDecisionReplay;
import com.aquacode.ctm.history.application.HistoricalDecisionUnreplayableException;
import com.aquacode.ctm.history.application.HistoricalReplayNotFoundException;
import com.aquacode.ctm.history.application.HistoricalReplayService;
import com.aquacode.ctm.history.application.HistoricalRuleReplay;
import com.aquacode.ctm.rules.RuleOutcome;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
    HistoryController.class,
    HistoryExceptionHandler.class,
    HistoricalReplayResponseMapper.class
})
class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HistoricalReplayService historicalReplayService;

    @Test
    void replay_shouldReturnHistoricalReplayResponse() throws Exception {
        var transactionId = "TX-123";

        when(historicalReplayService.replay(transactionId))
            .thenReturn(new HistoricalDecisionReplay(
                transactionId,
                Instant.parse("2025-03-01T10:00:00Z"),
                "2025-Q1",
                Decision.APPROVED,
                List.of(
                    new HistoricalRuleReplay(
                        "AML-001",
                        "v1",
                        RuleOutcome.PASS,
                        "Transaction amount below threshold"
                    ),
                    new HistoricalRuleReplay(
                        "PEP-001",
                        "v1",
                        RuleOutcome.PASS,
                        "Customer not identified as PEP"
                    )
                ),
                Instant.parse("2026-06-13T15:40:00Z")
            ));

        mockMvc.perform(get("/api/v1/history/transactions/{transactionId}", transactionId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.transactionId").value("TX-123"))
            .andExpect(jsonPath("$.historicalTimestamp").value("2025-03-01T10:00:00Z"))
            .andExpect(jsonPath("$.ruleSetVersion").value("2025-Q1"))
            .andExpect(jsonPath("$.decision").value("APPROVED"))
            .andExpect(jsonPath("$.rules[0].ruleCode").value("AML-001"))
            .andExpect(jsonPath("$.rules[0].version").value("v1"))
            .andExpect(jsonPath("$.rules[0].outcome").value("PASS"))
            .andExpect(jsonPath("$.rules[0].explanation").value("Transaction amount below threshold"))
            .andExpect(jsonPath("$.rules[1].ruleCode").value("PEP-001"))
            .andExpect(jsonPath("$.rules[1].version").value("v1"))
            .andExpect(jsonPath("$.rules[1].outcome").value("PASS"))
            .andExpect(jsonPath("$.rules[1].explanation").value("Customer not identified as PEP"))
            .andExpect(jsonPath("$.replayedAt").value("2026-06-13T15:40:00Z"));
    }

    @Test
    void replay_shouldReturnNotFoundWhenHistoricalTransactionDoesNotExist() throws Exception {
        var transactionId = "TX-MISSING";

        when(historicalReplayService.replay(transactionId))
            .thenThrow(new HistoricalReplayNotFoundException(transactionId));

        mockMvc.perform(get("/api/v1/history/transactions/{transactionId}", transactionId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("TRANSACTION_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Historical transaction TX-MISSING was not found"));
    }

    @Test
    void replay_shouldReturnUnprocessableEntityWhenHistoricalDecisionCannotBeReplayed() throws Exception {
        var transactionId = "TX-123";
        var historicalTimestamp = Instant.parse("2025-03-01T10:00:00Z");

        when(historicalReplayService.replay(transactionId))
            .thenThrow(new HistoricalDecisionUnreplayableException(
                transactionId,
                historicalTimestamp,
                new RuntimeException("Rule set missing")
            ));

        mockMvc.perform(get("/api/v1/history/transactions/{transactionId}", transactionId))
            .andExpect(status().isUnprocessableContent())
            .andExpect(jsonPath("$.code").value("UNREPLAYABLE_DECISION"))
            .andExpect(jsonPath("$.message").value(containsString("Historical policy version for transaction TX-123")))
            .andExpect(jsonPath("$.message").value(containsString("2025-03-01T10:00:00Z")))
            .andExpect(jsonPath("$.message").value(containsString("cannot be reconstructed")));
    }

}