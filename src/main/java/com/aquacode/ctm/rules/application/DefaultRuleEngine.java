package com.aquacode.ctm.rules.application;

import com.aquacode.ctm.rules.RuleEngine;
import com.aquacode.ctm.rules.RuleEvaluationContext;
import com.aquacode.ctm.rules.RuleResult;
import com.aquacode.ctm.rules.RuleSet;

import java.util.List;

public class DefaultRuleEngine implements RuleEngine {
    @Override
    public List<RuleResult> evaluate(RuleSet ruleSet, RuleEvaluationContext context) {
        return ruleSet.rules().stream()
            .map(rule -> rule.evaluate(context))
            .toList();
    }
}
