package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.ComplianceDecision;
import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.evaluation.Transaction;
import com.aquacode.ctm.rules.RuleEngine;
import com.aquacode.ctm.rules.RuleEvaluationContext;
import com.aquacode.ctm.rules.RuleFixtures;
import com.aquacode.ctm.rules.RuleSet;
import com.aquacode.ctm.rules.RuleSetResolver;
import com.aquacode.ctm.shared.DecisionMadeEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.aquacode.ctm.evaluation.EvaluationFixtures.approvedDecision;
import static com.aquacode.ctm.evaluation.EvaluationFixtures.failedDecision;
import static com.aquacode.ctm.rules.RuleFixtures.failedResult;
import static com.aquacode.ctm.rules.RuleFixtures.passedResult;
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

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EvaluationService evaluationService;

    @Test
    void evaluate_shouldReturnApprovedDecisionWhenAllRulesPass() {
        var transaction = givenTransaction();
        var ruleSet = RuleFixtures.ruleSet();
        var context = transaction.toEvaluationContext();
        var expectedDecision = approvedDecision();

        when(ruleSetResolver.resolve(transaction.transactionTimestamp()))
            .thenReturn(ruleSet);
        when(ruleEngine.evaluate(ruleSet, context))
            .thenReturn(List.of(passedResult()));
        when(decisionFactory.create(List.of(passedResult()), "v1"))
            .thenReturn(expectedDecision);

        var decision = evaluationService.evaluate(transaction);

        assertThat(decision.decision()).isEqualTo(Decision.APPROVED);
        assertThat(decision.ruleSetVersion()).isEqualTo("v1");
        assertThat(decision.results()).containsExactly(passedResult());

        verifyInteractions(transaction, ruleSet, context, expectedDecision);
    }

    @Test
    void evaluate_shouldReturnRejectedDecisionWhenAnyRuleFails() {
        var transaction = givenTransaction();
        var ruleSet = RuleFixtures.ruleSet();
        var context = transaction.toEvaluationContext();
        var expectedDecision = failedDecision();

        when(ruleSetResolver.resolve(transaction.transactionTimestamp()))
            .thenReturn(ruleSet);
        when(ruleEngine.evaluate(ruleSet, context))
            .thenReturn(List.of(failedResult()));
        when(decisionFactory.create(List.of(failedResult()), "v1"))
            .thenReturn(expectedDecision);

        var decision = evaluationService.evaluate(transaction);

        assertThat(decision.decision()).isEqualTo(Decision.REJECTED);
        assertThat(decision.ruleSetVersion()).isEqualTo("v1");
        assertThat(decision.results()).containsExactly(failedResult());

        verifyInteractions(transaction, ruleSet, context, expectedDecision);
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

    private void verifyInteractions(Transaction transaction, RuleSet ruleSet, RuleEvaluationContext context, ComplianceDecision decision) {
        verify(ruleSetResolver).resolve(transaction.transactionTimestamp());
        verify(ruleEngine).evaluate(ruleSet, context);
        verify(decisionFactory).create(decision.results(), ruleSet.version());

        var eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertThat(eventCaptor.getValue())
            .isInstanceOfSatisfying(DecisionMadeEvent.class, event -> {
                assertThat(event.transactionId()).isEqualTo(transaction.transactionId());
                assertThat(event.decisionId()).isEqualTo(decision.decisionId());
                assertThat(event.decision()).isEqualTo(decision.decision().name());
                assertThat(event.ruleSetVersion()).isEqualTo(decision.ruleSetVersion());
                assertThat(event.evaluatedAt()).isEqualTo(decision.evaluatedAt());
            });
    }
}