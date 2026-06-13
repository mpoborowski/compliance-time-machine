package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.ComplianceDecision;
import com.aquacode.ctm.evaluation.Transaction;
import com.aquacode.ctm.rules.RuleEngine;
import com.aquacode.ctm.rules.RuleSetResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final RuleSetResolver ruleSetResolver;
    private final RuleEngine ruleEngine;
    private final ComplianceDecisionFactory decisionFactory;

    public ComplianceDecision evaluate(Transaction transaction) {
        var ruleSet = ruleSetResolver.resolve(transaction.transactionTimestamp());
        var results = ruleEngine.evaluate(ruleSet, transaction.toEvaluationContext());

        return decisionFactory.create(results, ruleSet.version());
    }
}
