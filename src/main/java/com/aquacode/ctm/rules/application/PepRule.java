package com.aquacode.ctm.rules.application;

import com.aquacode.ctm.rules.Rule;
import com.aquacode.ctm.rules.RuleEvaluationContext;
import com.aquacode.ctm.rules.RuleMetadata;
import com.aquacode.ctm.rules.RuleResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PepRule implements Rule {

    static final String FAILED_EXPLANATION = "Customer identified as PEP";
    static final String PASSED_EXPLANATION = "Customer not identified as PEP";

    private final RuleMetadata metadata;


    @Override
    public RuleResult evaluate(RuleEvaluationContext context) {
        return context.politicallyExposedPerson() ?
            failedResult(FAILED_EXPLANATION) :
            passedResult(PASSED_EXPLANATION);
    }

    @Override
    public RuleMetadata metadata() {
        return metadata;
    }
}
