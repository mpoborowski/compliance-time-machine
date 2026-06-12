package com.aquacode.ctm.evaluation.application;

import com.aquacode.ctm.evaluation.ComplianceDecision;
import com.aquacode.ctm.evaluation.Transaction;
import com.aquacode.ctm.evaluation.infrastructure.dto.EvaluateTransactionRequest;
import com.aquacode.ctm.evaluation.infrastructure.dto.EvaluateTransactionResponse;
import com.aquacode.ctm.evaluation.infrastructure.dto.TriggeredRuleResponse;
import com.aquacode.ctm.rules.RuleResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EvaluationMapper {

    EvaluationMapper INSTANCE = Mappers.getMapper(EvaluationMapper.class);

    Transaction fromEvaluationRequest(EvaluateTransactionRequest request);

    @Mapping(source = "ruleSetVersion", target = "appliedRuleSetVersion")
    @Mapping(source = "results", target = "triggeredRules")
    EvaluateTransactionResponse fromComplianceDecision(ComplianceDecision decision);

    @Mapping(source = "metadata.code", target = "ruleCode")
    @Mapping(source = "metadata.version", target = "ruleVersion")
    @Mapping(source = "outcome", target = "outcome")
    @Mapping(source = "explanation", target = "explanation")
    TriggeredRuleResponse fromRuleResult(RuleResult result);
}
