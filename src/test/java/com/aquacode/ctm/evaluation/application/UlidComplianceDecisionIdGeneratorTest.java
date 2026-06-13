package com.aquacode.ctm.evaluation.application;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UlidComplianceDecisionIdGeneratorTest {

    private final ComplianceDecisionIdGenerator generator = new UlidComplianceDecisionIdGenerator();

    @Test
    void generate_shouldReturnDecisionIdWithPrefix() {
        var result = generator.generate();

        assertThat(result).startsWith("dec_");
    }

    @Test
    void generate_shouldReturnDecisionIdWithUlidLength() {
        var result = generator.generate();

        assertThat(result).hasSize(30);
    }

    @Test
    void generate_shouldReturnDifferentDecisionIds() {
        var first = generator.generate();
        var second = generator.generate();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void generate_shouldReturnLexicographicallySortableDecisionIds() {
        var first = generator.generate();
        var second = generator.generate();

        assertThat(first).isLessThan(second);
    }
}