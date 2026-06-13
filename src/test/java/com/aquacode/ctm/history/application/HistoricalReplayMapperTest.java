package com.aquacode.ctm.history.application;

import com.aquacode.ctm.audit.HistoricalTransactionAuditRecord;
import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.rules.RuleMetadata;
import com.aquacode.ctm.rules.RuleOutcome;
import com.aquacode.ctm.rules.RuleResult;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HistoricalReplayMapperTest {

    private final HistoricalReplayMapper mapper = Mappers.getMapper(HistoricalReplayMapper.class);

    @Test
    void toTransaction_shouldMapHistoricalTransactionAuditRecordToTransaction() {
        var auditRecord = new HistoricalTransactionAuditRecord(
            "tx-1",
            "customer-1",
            "PL",
            BigDecimal.TEN,
            false,
            Instant.parse("2025-01-01T00:00:00Z"),
            "dec-1",
            "v1",
            Decision.APPROVED,
            Instant.parse("2025-01-01T12:00:00Z")
        );

        var transaction = mapper.toTransaction(auditRecord);

        assertThat(transaction.transactionId()).isEqualTo("tx-1");
        assertThat(transaction.customerId()).isEqualTo("customer-1");
        assertThat(transaction.country()).isEqualTo("PL");
        assertThat(transaction.amount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(transaction.politicallyExposedPerson()).isFalse();
        assertThat(transaction.transactionTimestamp()).isEqualTo(Instant.parse("2025-01-01T00:00:00Z"));
    }

    @Test
    void toHistoricalRuleReplays_shouldMapRuleResultsToHistoricalRuleReplays() {
        var result = RuleResult.builder()
            .metadata(new RuleMetadata("AML-001", "v1", "Amount threshold"))
            .outcome(RuleOutcome.PASS)
            .explanation("Transaction amount below threshold")
            .build();

        var rules = mapper.toHistoricalRuleReplays(List.of(result));

        assertThat(rules).hasSize(1);

        var rule = rules.getFirst();
        assertThat(rule.ruleCode()).isEqualTo("AML-001");
        assertThat(rule.version()).isEqualTo("v1");
        assertThat(rule.outcome()).isEqualTo(RuleOutcome.PASS);
        assertThat(rule.explanation()).isEqualTo("Transaction amount below threshold");
    }
}