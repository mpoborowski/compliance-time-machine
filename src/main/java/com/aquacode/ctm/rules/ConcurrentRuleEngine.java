package com.aquacode.ctm.rules;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;

@Component
public class ConcurrentRuleEngine implements RuleEngine {
    @Override
    public List<RuleResult> evaluate(RuleSet ruleSet, RuleEvaluationContext context) {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var futures = ruleSet.rules()
                .stream()
                .map(rule -> scope.fork( () -> rule.evaluate(context)))
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
