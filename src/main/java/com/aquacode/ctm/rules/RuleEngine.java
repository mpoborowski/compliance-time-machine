package com.aquacode.ctm.rules;

import java.util.List;

public interface RuleEngine {

    List<RuleResult> evaluate(RuleSet ruleSet, RuleEvaluationContext context);
}
