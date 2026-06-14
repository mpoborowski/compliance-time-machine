package com.aquacode.ctm.shared;

import com.aquacode.ctm.evaluation.Decision;

import java.math.BigDecimal;
import java.time.Instant;

public final class DecisionMadeFixtures {

    public static DecisionMadeEvent decisionMadeEvent() {
        return DecisionMadeEvent.builder()
            .transactionId("tx-1")
            .customerId("customer-1")
            .politicallyExposedPerson(false)
            .amount(BigDecimal.TEN)
            .country("PL")
            .ruleSetVersion("v1")
            .decisionId("dec_1")
            .decision(Decision.APPROVED.name())
            .transactionTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
            .evaluatedAt(Instant.parse("2025-01-01T12:00:00Z"))
            .build();
    }
}
