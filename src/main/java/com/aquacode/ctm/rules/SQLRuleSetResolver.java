package com.aquacode.ctm.rules;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SQLRuleSetResolver implements RuleSetResolver {

    @Override
    public RuleSet resolve(Instant pointInTime) {
        return null;
    }
}
