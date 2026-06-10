package com.aquacode.ctm.rules;

public interface Rule {

    RuleResult evaluate(RuleEvaluationContext context);

    RuleMetadata metadata();
}
