package com.aquacode.ctm.history.application;

import com.aquacode.ctm.audit.AuditHistory;
import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.evaluation.EvaluationFixtures;
import com.aquacode.ctm.rules.RuleEngine;
import com.aquacode.ctm.rules.RuleMetadata;
import com.aquacode.ctm.rules.RuleOutcome;
import com.aquacode.ctm.rules.RuleResult;
import com.aquacode.ctm.rules.RuleSet;
import com.aquacode.ctm.rules.RuleSetNotFoundException;
import com.aquacode.ctm.rules.RuleSetResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.aquacode.ctm.audit.AuditFixtures.historicalTransactionAuditRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricalReplayServiceTest {

    private static final Instant HISTORICAL_TIMESTAMP = Instant.parse("2025-01-01T12:00:00Z");
    private static final Instant REPLAYED_AT = Instant.parse("2026-06-13T15:40:00Z");

    @Mock
    private AuditHistory auditHistory;

    @Mock
    private RuleSetResolver ruleSetResolver;

    @Mock
    private RuleEngine ruleEngine;

    @Mock
    private HistoricalReplayMapper mapper;

    @Mock
    private Clock clock;

    @InjectMocks
    private HistoricalReplayService historicalReplayService;

    @Test
    void replay_shouldReconstructApprovedHistoricalDecision() {
        var transactionId = "tx-1";
        var auditRecord = historicalTransactionAuditRecord();
        var transaction = EvaluationFixtures.transaction();
        var ruleSet = givenRuleSet();
        var ruleResults = List.of(
            givenRuleResult("AML-001", "v1", RuleOutcome.PASS, "Transaction amount below threshold"),
            givenRuleResult("PEP-001", "v1", RuleOutcome.PASS, "Customer is not PEP")
        );
        var historicalRuleReplays = List.of(
            new HistoricalRuleReplay("AML-001", "v1", RuleOutcome.PASS, "Transaction amount below threshold"),
            new HistoricalRuleReplay("PEP-001", "v1", RuleOutcome.PASS, "Customer is not PEP")
        );

        when(auditHistory.findTransaction(transactionId))
            .thenReturn(Optional.of(auditRecord));
        when(ruleSetResolver.resolve(HISTORICAL_TIMESTAMP))
            .thenReturn(ruleSet);
        when(mapper.toTransaction(auditRecord))
            .thenReturn(transaction);
        when(ruleEngine.evaluate(ruleSet, transaction.toEvaluationContext()))
            .thenReturn(ruleResults);
        when(mapper.toHistoricalRuleReplays(ruleResults))
            .thenReturn(historicalRuleReplays);
        when(clock.instant()).thenReturn(REPLAYED_AT);

        var replay = historicalReplayService.replay(transactionId);

        assertThat(replay.transactionId()).isEqualTo(transactionId);
        assertThat(replay.historicalTimestamp()).isEqualTo(HISTORICAL_TIMESTAMP);
        assertThat(replay.ruleSetVersion()).isEqualTo("2025-Q1");
        assertThat(replay.decision()).isEqualTo(Decision.APPROVED);
        assertThat(replay.rules()).isEqualTo(historicalRuleReplays);
        assertThat(replay.replayedAt()).isEqualTo(REPLAYED_AT);

        verify(auditHistory).findTransaction(transactionId);
        verify(ruleSetResolver).resolve(HISTORICAL_TIMESTAMP);
        verify(mapper).toTransaction(auditRecord);
        verify(ruleEngine).evaluate(ruleSet, transaction.toEvaluationContext());
        verify(mapper).toHistoricalRuleReplays(ruleResults);
        verify(clock).instant();
    }

    @Test
    void replay_shouldReconstructRejectedHistoricalDecisionWhenAnyRuleFailed() {
        var transactionId = "tx-1";
        var auditRecord = historicalTransactionAuditRecord();
        var transaction = EvaluationFixtures.transaction();
        var ruleSet = givenRuleSet();
        var ruleResults = List.of(
            givenRuleResult("AML-001", "v1", RuleOutcome.PASS, "Transaction amount below threshold"),
            givenRuleResult("PEP-001", "v1", RuleOutcome.FAIL, "Customer is PEP")
        );
        var historicalRuleReplays = List.of(
            new HistoricalRuleReplay("AML-001", "v1", RuleOutcome.PASS, "Transaction amount below threshold"),
            new HistoricalRuleReplay("PEP-001", "v1", RuleOutcome.FAIL, "Customer is PEP")
        );

        when(auditHistory.findTransaction(transactionId))
            .thenReturn(Optional.of(auditRecord));
        when(ruleSetResolver.resolve(HISTORICAL_TIMESTAMP))
            .thenReturn(ruleSet);
        when(mapper.toTransaction(auditRecord))
            .thenReturn(transaction);
        when(ruleEngine.evaluate(ruleSet, transaction.toEvaluationContext()))
            .thenReturn(ruleResults);
        when(mapper.toHistoricalRuleReplays(ruleResults))
            .thenReturn(historicalRuleReplays);
        when(clock.instant()).thenReturn(REPLAYED_AT);

        var replay = historicalReplayService.replay(transactionId);

        assertThat(replay.decision()).isEqualTo(Decision.REJECTED);
        assertThat(replay.rules()).isEqualTo(historicalRuleReplays);
        assertThat(replay.replayedAt()).isEqualTo(REPLAYED_AT);

        verify(clock).instant();
    }

    @Test
    void replay_shouldThrowNotFoundWhenAuditRecordDoesNotExist() {
        var transactionId = "tx-missing";

        when(auditHistory.findTransaction(transactionId))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> historicalReplayService.replay(transactionId))
            .isInstanceOf(HistoricalReplayNotFoundException.class)
            .hasMessage("Historical transaction tx-missing was not found");

        verify(auditHistory).findTransaction(transactionId);
        verifyNoInteractions(ruleSetResolver, ruleEngine, mapper, clock);
    }

    @Test
    void replay_shouldThrowUnreplayableWhenHistoricalRuleSetCannotBeResolved() {
        var transactionId = "tx-1";
        var auditRecord = historicalTransactionAuditRecord();
        var cause = new RuleSetNotFoundException(HISTORICAL_TIMESTAMP);

        when(auditHistory.findTransaction(transactionId))
            .thenReturn(Optional.of(auditRecord));
        when(ruleSetResolver.resolve(HISTORICAL_TIMESTAMP))
            .thenThrow(cause);

        assertThatThrownBy(() -> historicalReplayService.replay(transactionId))
            .isInstanceOf(HistoricalDecisionUnreplayableException.class)
            .hasMessage(
                "Historical policy version for transaction tx-1 at 2025-01-01T12:00:00Z cannot be reconstructed"
            )
            .hasCause(cause);

        verify(auditHistory).findTransaction(transactionId);
        verify(ruleSetResolver).resolve(HISTORICAL_TIMESTAMP);
        verifyNoInteractions(ruleEngine, mapper, clock);
    }

    private static RuleSet givenRuleSet() {
        return new RuleSet(
            "2025-Q1",
            Instant.parse("2025-01-01T00:00:00Z"),
            List.of()
        );
    }

    private static RuleResult givenRuleResult(String ruleCode, String ruleVersion, RuleOutcome outcome, String explanation) {
        return RuleResult.builder()
            .metadata(new RuleMetadata(ruleCode, ruleVersion, "Test rule"))
            .outcome(outcome)
            .explanation(explanation)
            .build();
    }
}