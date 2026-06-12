package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.ComplianceDecision;
import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.evaluation.Transaction;
import com.aquacode.ctm.evaluation.infrastructure.dto.EvaluateTransactionRequest;
import com.aquacode.ctm.evaluation.infrastructure.dto.EvaluateTransactionResponse;
import com.aquacode.ctm.evaluation.infrastructure.dto.TriggeredRuleResponse;
import com.aquacode.ctm.rules.RuleMetadata;
import com.aquacode.ctm.rules.RuleOutcome;
import com.aquacode.ctm.rules.RuleResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationMapperTest {

    private final EvaluationMapper mapper = EvaluationMapper.INSTANCE;

    @Test
    void fromEvaluationRequest_shouldMapEvaluateTransactionRequestToTransaction() {
        Instant now = Instant.now();
        EvaluateTransactionRequest request = new EvaluateTransactionRequest(
            "tx-123",
            "cust-999",
            "PL",
            new BigDecimal("1500.50"),
            true,
            now
        );

        Transaction result = mapper.fromEvaluationRequest(request);

        assertThat(result).isNotNull();
        assertThat(result.transactionId()).isEqualTo("tx-123");
        assertThat(result.customerId()).isEqualTo("cust-999");
        assertThat(result.country()).isEqualTo("PL");
        assertThat(result.amount()).isEqualByComparingTo("1500.50");
        assertThat(result.politicallyExposedPerson()).isTrue();
        assertThat(result.transactionTimestamp()).isEqualTo(now);
    }

    @Test
    void fromComplianceDecision_shouldMapComplianceDecisionToEvaluateTransactionResponse() {
        Instant now = Instant.now();

        RuleMetadata metadata = new RuleMetadata("RULE_01", "v2", "Limit check");
        RuleResult ruleResult = new RuleResult(metadata, RuleOutcome.FAIL, "Amount exceeded");

        ComplianceDecision decision = new ComplianceDecision(
            "dec-456",
            Decision.REJECTED,
            "v1.0.4",
            now,
            List.of(ruleResult)
        );

        EvaluateTransactionResponse response = mapper.fromComplianceDecision(decision);

        assertThat(response).isNotNull();
        assertThat(response.decisionId()).isEqualTo("dec-456");
        assertThat(response.decision()).isEqualTo("REJECTED");
        assertThat(response.appliedRuleSetVersion()).isEqualTo("v1.0.4");
        assertThat(response.evaluatedAt()).isEqualTo(now);

        assertThat(response.triggeredRules()).hasSize(1);
        TriggeredRuleResponse ruleResponse = response.triggeredRules().getFirst();
        assertThat(ruleResponse.ruleCode()).isEqualTo("RULE_01");
        assertThat(ruleResponse.ruleVersion()).isEqualTo("v2");
        assertThat(ruleResponse.outcome()).isEqualTo("FAIL");
        assertThat(ruleResponse.explanation()).isEqualTo("Amount exceeded");
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertThat(mapper.fromEvaluationRequest(null)).isNull();
        assertThat(mapper.fromComplianceDecision(null)).isNull();
    }
}