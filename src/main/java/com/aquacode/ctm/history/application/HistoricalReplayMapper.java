package com.aquacode.ctm.history.application;

import com.aquacode.ctm.audit.HistoricalTransactionAuditRecord;
import com.aquacode.ctm.evaluation.Transaction;
import com.aquacode.ctm.rules.RuleResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoricalReplayMapper {

    Transaction toTransaction(HistoricalTransactionAuditRecord auditRecord);

    List<HistoricalRuleReplay> toHistoricalRuleReplays(List<RuleResult> results);

    @Mapping(source = "metadata.code", target = "ruleCode")
    @Mapping(source = "metadata.version", target = "version")
    @Mapping(source = "outcome", target = "outcome")
    @Mapping(source = "explanation", target = "explanation")
    HistoricalRuleReplay toHistoricalRuleReplay(RuleResult result);
}