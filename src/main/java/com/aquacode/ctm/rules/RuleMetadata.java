package com.aquacode.ctm.rules;

import lombok.Builder;

@Builder
public record RuleMetadata(
    String code,
    String version,
    String description
) {
}
