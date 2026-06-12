package com.aquacode.ctm.evaluation.infrastructure.dto;

public record TriggeredRuleResponse(String ruleCode,
                                    String ruleVersion,
                                    String outcome,
                                    String explanation) {
}
