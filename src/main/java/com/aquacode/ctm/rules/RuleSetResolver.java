package com.aquacode.ctm.rules;

import java.time.Instant;

public interface RuleSetResolver {

    RuleSet resolve(Instant pointInTime);
}
