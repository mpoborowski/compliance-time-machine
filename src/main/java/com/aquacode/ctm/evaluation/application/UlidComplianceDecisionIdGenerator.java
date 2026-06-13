package com.aquacode.ctm.evaluation.application;

import de.huxhorn.sulky.ulid.ULID;
import org.springframework.stereotype.Component;

@Component
public class UlidComplianceDecisionIdGenerator implements ComplianceDecisionIdGenerator {

    private static final String PREFIX = "dec_";
    private final ULID ulid = new ULID();

    @Override
    public String generate() {
        return PREFIX + ulid.nextULID();
    }
}