package com.aquacode.ctm.rules;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RuleEvaluationContext(
    String country,
    BigDecimal amount,
    boolean politicallyExposedPerson
) {
}
