package com.aquacode.ctm.rules;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record RuleSet(String version,
                      Instant effectiveFrom,
                      List<Rule> rules
) {
}
