package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.ComplianceDecision;
import com.aquacode.ctm.evaluation.Transaction;
import com.aquacode.ctm.rules.RuleEngine;
import com.aquacode.ctm.rules.RuleSetResolver;
import com.aquacode.ctm.shared.DecisionMadeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final RuleSetResolver ruleSetResolver;
    private final RuleEngine ruleEngine;
    private final ComplianceDecisionFactory decisionFactory;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ComplianceDecision evaluate(Transaction transaction) {
        var ruleSet = ruleSetResolver.resolve(transaction.transactionTimestamp());
        var results = ruleEngine.evaluate(ruleSet, transaction.toEvaluationContext());

        var decision = decisionFactory.create(results, ruleSet.version());

        eventPublisher.publishEvent(new DecisionMadeEvent(
            transaction.transactionId(),
            decision.decisionId(),
            decision.decision().name(),
            decision.ruleSetVersion(),
            decision.evaluatedAt()
        ));

        return decision;
    }
}
