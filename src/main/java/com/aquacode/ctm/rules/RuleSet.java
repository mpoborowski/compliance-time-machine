package com.aquacode.ctm.rules;

import java.time.Instant;
import java.util.List;

public record RuleSet(String version, Instant effectiveFrom, List<Rule> rules ) {
}
