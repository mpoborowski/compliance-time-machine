package com.aquacode.ctm.rules;

import java.time.Instant;

public class RuleSetNotFoundException extends RuntimeException {

    public RuleSetNotFoundException(Instant pointInTime) {
        super("No active rule set found for point in time: " + pointInTime);
    }
}
