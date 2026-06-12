package com.aquacode.ctm.rules.application;

import com.aquacode.ctm.rules.Rule;
import com.aquacode.ctm.rules.RuleEngine;
import com.aquacode.ctm.rules.RuleEvaluationContext;
import com.aquacode.ctm.rules.RuleMetadata;
import com.aquacode.ctm.rules.RuleResult;
import com.aquacode.ctm.rules.RuleSet;
import lombok.SneakyThrows;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.aquacode.ctm.rules.RulesTestDataProvider.context;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConcurrentRuleEngineTest extends RuleEngineContractTest {

    private final RuleEngine engine = new ConcurrentRuleEngine();

    @Override
    protected RuleEngine ruleEngine() {
        return engine;
    }

    @Test
    void evaluate_shouldExecuteRulesInParallel() {
        AtomicInteger counter = new AtomicInteger();
        Rule slowRule = new Rule() {
            @SneakyThrows
            @Override
            public RuleResult evaluate(RuleEvaluationContext context) {
                sleep(500);
                counter.incrementAndGet();
                return passedResult();
            }

            @Override
            public RuleMetadata metadata() {
                return new RuleMetadata("T1", "v1", "slow");
            }
        };

        Rule slowRule2 = new Rule() {
            @SneakyThrows
            @Override
            public RuleResult evaluate(RuleEvaluationContext context) {
                sleep(500);
                counter.incrementAndGet();
                return passedResult();
            }

            @Override
            public RuleMetadata metadata() {
                return new RuleMetadata("T2", "v1", "slow");
            }
        };

        var ruleSet = new RuleSet("v1", Instant.now(), List.of(slowRule, slowRule2));
        long start = System.currentTimeMillis();

        List<RuleResult> results = engine.evaluate(ruleSet, context(BigDecimal.ONE, "PL", false));
        long duration = System.currentTimeMillis() - start;

        assertThat(results).hasSize(2);
        assertThat(counter.get()).isEqualTo(2);

        // Sequential would have ~1000ms
        assertThat(duration).isLessThan(800);
    }

    @Test
    void evaluate_shouldCompleteAllRulesEventually() {

        List<String> executed = new CopyOnWriteArrayList<>();

        Rule r1 = ruleWithDelay("R1", executed, 300);
        Rule r2 = ruleWithDelay("R2", executed, 200);
        Rule r3 = ruleWithDelay("R3", executed, 350);

        var ruleSet = new RuleSet("v1", Instant.now(), List.of(r1, r2, r3));

        engine.evaluate(ruleSet, context(BigDecimal.ONE, "PL", false));

        Awaitility.await()
            .atMost(Duration.ofSeconds(2))
            .untilAsserted(() -> assertThat(executed).containsExactlyInAnyOrder("R1", "R2", "R3"));
    }

    @Test
    void evaluate_shouldFailWhenOneRuleFails() {
        Rule badRule = new Rule() {
            @Override
            public RuleResult evaluate(RuleEvaluationContext context) {
                throw new RuntimeException("rule failure");
            }

            @Override
            public RuleMetadata metadata() {
                return new RuleMetadata("ERR", "v1", "error");
            }
        };

        var ruleSet = new RuleSet("v1", Instant.now(), List.of(badRule));

        assertThatThrownBy(() -> engine.evaluate(ruleSet, context(BigDecimal.ONE, "PL", false)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Failed to process rules");
    }

    private static Rule ruleWithDelay(String id, List<String> executed, long ms) {
        return new Rule() {
            @SneakyThrows
            @Override
            public RuleResult evaluate(RuleEvaluationContext context) {
                sleep(ms);
                executed.add(id);
                return passedResult();
            }

            @Override
            public RuleMetadata metadata() {
                return new RuleMetadata(id, "v1", "test");
            }
        };
    }

}
