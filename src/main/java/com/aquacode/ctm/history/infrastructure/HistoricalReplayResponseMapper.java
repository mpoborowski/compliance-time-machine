package com.aquacode.ctm.history.infrastructure;

import com.aquacode.ctm.history.application.HistoricalDecisionReplay;
import com.aquacode.ctm.history.application.HistoricalRuleReplay;
import com.aquacode.ctm.history.infrastructure.dto.HistoricalReplayResponse;
import com.aquacode.ctm.history.infrastructure.dto.HistoricalRuleReplayResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HistoricalReplayResponseMapper {

    HistoricalReplayResponse toResponse(HistoricalDecisionReplay replay);

    @Mapping(source = "outcome", target = "outcome")
    HistoricalRuleReplayResponse toResponse(HistoricalRuleReplay ruleReplay);
}