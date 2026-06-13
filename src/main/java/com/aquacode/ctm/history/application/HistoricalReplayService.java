package com.aquacode.ctm.history.application;

import com.aquacode.ctm.audit.AuditHistory;
import com.aquacode.ctm.evaluation.Decision;
import com.aquacode.ctm.rules.RuleEngine;
import com.aquacode.ctm.rules.RuleResult;
import com.aquacode.ctm.rules.RuleSet;
import com.aquacode.ctm.rules.RuleSetNotFoundException;
import com.aquacode.ctm.rules.RuleSetResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoricalReplayService {

    private final AuditHistory auditHistory;
    private final RuleSetResolver ruleSetResolver;
    private final RuleEngine ruleEngine;
    private final HistoricalReplayMapper mapper;
    private final Clock clock;

    @Transactional(readOnly = true)
    public HistoricalDecisionReplay replay(String transactionId) {
        var auditRecord = auditHistory.findTransaction(transactionId)
            .orElseThrow(() -> new HistoricalReplayNotFoundException(transactionId));

        var historicalTimestamp = auditRecord.evaluatedAt();
        var ruleSet = resolveRuleSet(transactionId, historicalTimestamp);
        var transaction = mapper.toTransaction(auditRecord);
        var results = ruleEngine.evaluate(ruleSet, transaction.toEvaluationContext());
        var decision = reconstructDecision(results);
        var replayedAt = Instant.now(clock);

        log.info(
            "Historical replay completed for transactionId={}, historicalTimestamp={}, ruleSetVersion={}, decision={}, replayedAt={}",
            transactionId,
            historicalTimestamp,
            ruleSet.version(),
            decision,
            replayedAt
        );

        return new HistoricalDecisionReplay(
            transactionId,
            historicalTimestamp,
            ruleSet.version(),
            decision,
            mapper.toHistoricalRuleReplays(results),
            replayedAt
        );
    }

    private RuleSet resolveRuleSet(String transactionId, Instant historicalTimestamp) {
        try {
            return ruleSetResolver.resolve(historicalTimestamp);
        } catch (RuleSetNotFoundException exception) {
            throw new HistoricalDecisionUnreplayableException(
                transactionId,
                historicalTimestamp,
                exception
            );
        }
    }

    private static Decision reconstructDecision(List<RuleResult> results) {
        var rejected = results.stream().anyMatch(RuleResult::failed);
        return rejected ? Decision.REJECTED : Decision.APPROVED;
    }
}