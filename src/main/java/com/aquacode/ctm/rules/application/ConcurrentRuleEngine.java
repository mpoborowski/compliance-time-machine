package com.aquacode.ctm.rules.application;

import com.aquacode.ctm.rules.RuleEngine;
import com.aquacode.ctm.rules.RuleEvaluationContext;
import com.aquacode.ctm.rules.RuleResult;
import com.aquacode.ctm.rules.RuleSet;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;

public class ConcurrentRuleEngine implements RuleEngine {
    @Override
    public List<RuleResult> evaluate(RuleSet ruleSet, RuleEvaluationContext context) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var futures = ruleSet.rules()
                .stream()
                .map(rule -> scope.fork(() -> rule.evaluate(context)))
                .toList();

            scope.join();
            scope.throwIfFailed();

            return futures.stream()
                .map(StructuredTaskScope.Subtask::get)
                .toList();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to process rules", ex);
        }
    }
}
