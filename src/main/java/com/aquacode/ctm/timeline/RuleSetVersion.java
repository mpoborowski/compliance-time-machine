package com.aquacode.ctm.timeline;

import java.time.Instant;

public record RuleSetVersion (
    String version,
    Instant effectiveFrom
){
}
