package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.ComplianceDecision;
import com.aquacode.ctm.evaluation.Transaction;
import com.aquacode.ctm.evaluation.infrastructure.dto.EvaluateTransactionRequest;
import com.aquacode.ctm.evaluation.infrastructure.dto.EvaluateTransactionResponse;
import com.aquacode.ctm.evaluation.infrastructure.dto.TriggeredRuleResponse;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static com.aquacode.ctm.evaluation.EvaluationFixtures.evaluateTransactionRequest;
import static com.aquacode.ctm.evaluation.EvaluationFixtures.failedDecision;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EvaluationMapperTest {

    private final EvaluationMapper mapper = Mappers.getMapper(EvaluationMapper.class);

    @Test
    void fromEvaluationRequest_shouldMapEvaluateTransactionRequestToTransaction() {
        EvaluateTransactionRequest request = evaluateTransactionRequest();

        Transaction result = mapper.fromEvaluationRequest(request);

        assertAll(
            () -> assertThat(result).isNotNull(),
            () -> assertThat(result.transactionId()).isEqualTo("tx-1"),
            () -> assertThat(result.customerId()).isEqualTo("customer-1"),
            () -> assertThat(result.country()).isEqualTo("PL"),
            () -> assertThat(result.amount()).isEqualByComparingTo("10"),
            () -> assertThat(result.politicallyExposedPerson()).isFalse(),
            () -> assertThat(result.transactionTimestamp()).isEqualTo(Instant.parse("2025-01-01T12:00:00Z"))
        );
    }

    @Test
    void fromComplianceDecision_shouldMapComplianceDecisionToEvaluateTransactionResponse() {
        ComplianceDecision decision = failedDecision();

        EvaluateTransactionResponse response = mapper.fromComplianceDecision(decision);

        assertAll(
            () -> assertThat(response).isNotNull(),
            () -> assertThat(response.decisionId()).isEqualTo("dec_1"),
            () -> assertThat(response.decision()).isEqualTo("REJECTED"),
            () -> assertThat(response.appliedRuleSetVersion()).isEqualTo("v1"),
            () -> assertThat(response.evaluatedAt()).isEqualTo(Instant.parse("2025-01-01T12:00:00Z")),
            () -> assertThat(response.triggeredRules()).hasSize(1)
        );

        TriggeredRuleResponse ruleResponse = response.triggeredRules().getFirst();
        assertAll(
            () -> assertThat(ruleResponse.ruleCode()).isEqualTo("AML-001"),
            () -> assertThat(ruleResponse.ruleVersion()).isEqualTo("v1"),
            () -> assertThat(ruleResponse.outcome()).isEqualTo("FAIL"),
            () -> assertThat(ruleResponse.explanation()).isEqualTo("failure")
        );
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.fromEvaluationRequest(null)).isNull();
        assertThat(mapper.fromComplianceDecision(null)).isNull();
    }
}