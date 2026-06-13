package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.ComplianceDecision;
import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.evaluation.Transaction;
import com.aquacode.ctm.rules.RuleEngine;
import com.aquacode.ctm.rules.RuleEvaluationContext;
import com.aquacode.ctm.rules.RuleOutcome;
import com.aquacode.ctm.rules.RuleResult;
import com.aquacode.ctm.rules.RuleSet;
import com.aquacode.ctm.rules.RuleSetResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private RuleSetResolver ruleSetResolver;

    @Mock
    private RuleEngine ruleEngine;

    @Mock
    private ComplianceDecisionFactory decisionFactory;

    @InjectMocks
    private EvaluationService evaluationService;

    @Test
    void evaluate_shouldReturnApprovedDecisionWhenAllRulesPass() {
        var transaction = givenTransaction();
        var ruleSet = givenRuleSet("v1");
        var context = transaction.toEvaluationContext();

        when(ruleSetResolver.resolve(transaction.transactionTimestamp()))
            .thenReturn(ruleSet);
        when(ruleEngine.evaluate(ruleSet, context))
            .thenReturn(List.of(passedResult()));
        when(decisionFactory.create(List.of(passedResult()), "v1"))
            .thenReturn(givenApprovedDecision());

        var decision = evaluationService.evaluate(transaction);

        assertThat(decision.decision()).isEqualTo(Decision.APPROVED);
        assertThat(decision.ruleSetVersion()).isEqualTo("v1");
        assertThat(decision.results()).containsExactly(passedResult());

        verifyInteractions(transaction, ruleSet, context);
    }

    @Test
    void evaluate_shouldReturnRejectedDecisionWhenAnyRuleFails() {
        var transaction = givenTransaction();
        var ruleSet = givenRuleSet("v1");
        var context = transaction.toEvaluationContext();

        when(ruleSetResolver.resolve(transaction.transactionTimestamp()))
            .thenReturn(ruleSet);
        when(ruleEngine.evaluate(ruleSet, context))
            .thenReturn(List.of(failedResult()));
        when(decisionFactory.create(List.of(failedResult()), "v1"))
            .thenReturn(givenFailedDecision());

        var decision = evaluationService.evaluate(transaction);

        assertThat(decision.decision()).isEqualTo(Decision.REJECTED);
        assertThat(decision.ruleSetVersion()).isEqualTo("v1");
        assertThat(decision.results()).containsExactly(failedResult());

        verifyInteractions(transaction, ruleSet, context);
    }

    private static Transaction givenTransaction() {
        return new Transaction(
            "tx-1",
            "customer-1",
            "PL",
            BigDecimal.TEN,
            false,
            Instant.parse("2025-01-01T00:00:00Z")
        );
    }

    private static RuleSet givenRuleSet(String version) {
        return new RuleSet(
            version,
            Instant.parse("2025-01-01T00:00:00Z"),
            List.of()
        );
    }

    private static ComplianceDecision givenApprovedDecision() {
        return new ComplianceDecision("dec_1", Decision.APPROVED, "v1",
            Clock.fixed(Instant.parse("2025-01-01T12:00:00Z"), ZoneOffset.UTC).instant(),
            List.of(passedResult()));
    }

    private static ComplianceDecision givenFailedDecision() {
        return new ComplianceDecision("dec_1", Decision.REJECTED, "v1",
            Clock.fixed(Instant.parse("2025-01-01T12:00:00Z"), ZoneOffset.UTC).instant(),
            List.of(failedResult()));
    }

    private static RuleResult passedResult() {
        return new RuleResult(null, RuleOutcome.PASS, null);
    }

    private static RuleResult failedResult() {
        return new RuleResult(null, RuleOutcome.FAIL, "failure");
    }

    private void verifyInteractions(Transaction transaction, RuleSet ruleSet, RuleEvaluationContext context) {
        verify(ruleSetResolver).resolve(transaction.transactionTimestamp());
        verify(ruleEngine).evaluate(ruleSet, context);
    }
}