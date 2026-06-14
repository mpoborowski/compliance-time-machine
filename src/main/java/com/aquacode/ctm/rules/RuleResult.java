package com.aquacode.ctm.rules;

import lombok.Builder;

@Builder
public record RuleResult(RuleMetadata metadata,
                         RuleOutcome outcome,
                         String explanation
) {

    public boolean failed() {
        return outcome == RuleOutcome.FAIL;
    }
}
