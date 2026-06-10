package com.aquacode.ctm.rules;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultRuleEngine implements RuleEngine {
    @Override
    public List<RuleResult> evaluate(RuleSet ruleSet, RuleEvaluationContext context) {
        return ruleSet.rules().stream()
            .map(rule -> rule.evaluate(context))
            .toList();
    }
}
