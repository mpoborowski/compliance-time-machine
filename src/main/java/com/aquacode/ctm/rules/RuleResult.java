package com.aquacode.ctm.rules;

public record RuleResult(
    RuleMetadata metadata,
    RuleOutcome outcome,
    String explanation
) {
}
