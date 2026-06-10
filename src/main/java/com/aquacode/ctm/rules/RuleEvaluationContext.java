package com.aquacode.ctm.rules;

import java.math.BigDecimal;

public record RuleEvaluationContext(
    String country,
    BigDecimal amount,
    boolean politicallyExposedPerson
) {
}
