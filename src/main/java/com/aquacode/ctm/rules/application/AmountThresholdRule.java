package com.aquacode.ctm.rules.application;

import com.aquacode.ctm.rules.Rule;
import com.aquacode.ctm.rules.RuleEvaluationContext;
import com.aquacode.ctm.rules.RuleMetadata;
import com.aquacode.ctm.rules.RuleResult;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class AmountThresholdRule implements Rule {

    static final String FAILED_EXPLANATION = "Transaction amount %s exceeds threshold %s";
    static final String PASSED_EXPLANATION = "Transaction amount %s is within threshold %s";

    private final BigDecimal threshold;
    private final RuleMetadata metadata;

    @Override
    public RuleResult evaluate(RuleEvaluationContext context) {
        return context.amount().compareTo(threshold) > 0 ?
            failedResult(FAILED_EXPLANATION.formatted(context.amount(), threshold)) :
            passedResult(PASSED_EXPLANATION.formatted(context.amount(), threshold));
    }

    @Override
    public RuleMetadata metadata() {
        return metadata;
    }
}
