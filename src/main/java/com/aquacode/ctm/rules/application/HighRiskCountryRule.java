package com.aquacode.ctm.rules.application;

import com.aquacode.ctm.rules.Rule;
import com.aquacode.ctm.rules.RuleEvaluationContext;
import com.aquacode.ctm.rules.RuleMetadata;
import com.aquacode.ctm.rules.RuleResult;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
public class HighRiskCountryRule implements Rule {

    static final String FAILED_EXPLANATION = "Country %s classified as high risk";

    private final Set<String> highRiskCountries;
    private final RuleMetadata metadata;

    @Override
    public RuleResult evaluate(RuleEvaluationContext context) {
        return highRiskCountries.contains(context.country()) ?
            failedResult(FAILED_EXPLANATION.formatted(context.country())) :
            passedResult();
    }

    @Override
    public RuleMetadata metadata() {
        return metadata;
    }
}
