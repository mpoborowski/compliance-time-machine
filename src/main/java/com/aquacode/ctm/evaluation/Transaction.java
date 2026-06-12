package com.aquacode.ctm.evaluation;

import com.aquacode.ctm.rules.RuleEvaluationContext;

import java.math.BigDecimal;
import java.time.Instant;

public record Transaction(String transactionId,
                          String customerId,
                          String country,
                          BigDecimal amount,
                          boolean politicallyExposedPerson,
                          Instant transactionTimestamp) {

    public RuleEvaluationContext toEvaluationContext() {
        return new RuleEvaluationContext(country, amount, politicallyExposedPerson);
    }
}
