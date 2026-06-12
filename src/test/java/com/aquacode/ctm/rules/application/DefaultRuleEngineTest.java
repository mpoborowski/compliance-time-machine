package com.aquacode.ctm.rules.application;

import com.aquacode.ctm.rules.RuleEngine;

class DefaultRuleEngineTest extends RuleEngineContractTest {

    private final RuleEngine engine = new DefaultRuleEngine();

    @Override
    protected RuleEngine ruleEngine() {
        return engine;
    }

}
