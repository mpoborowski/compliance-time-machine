package com.aquacode.ctm.rules.infrastructure.persistence;

import com.aquacode.ctm.rules.Rule;
import com.aquacode.ctm.rules.RuleSet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RuleSetEntityMapper {

    default RuleSet toDomain(RuleSetEntity entity, List<Rule> rules) {
        if (entity == null) {
            return null;
        }
        return mapToDomain(entity, rules);
    }

    @Mapping(source = "entity.version", target = "version")
    @Mapping(source = "entity.effectiveFrom", target = "effectiveFrom")
    @Mapping(source = "rules", target = "rules")
    RuleSet mapToDomain(RuleSetEntity entity, List<Rule> rules);
}