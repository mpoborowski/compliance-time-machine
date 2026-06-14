package com.aquacode.ctm.rules;

public interface Rule {

    RuleResult evaluate(RuleEvaluationContext context);

    RuleMetadata metadata();

    default RuleResult failedResult(String explanation) {
        return RuleResult.builder()
            .outcome(RuleOutcome.FAIL)
            .metadata(metadata())
            .explanation(explanation)
            .build();
    }

    default RuleResult passedResult(String explanation) {
        return RuleResult.builder()
            .outcome(RuleOutcome.PASS)
            .metadata(metadata())
            .explanation(explanation)
            .build();
    }
}
