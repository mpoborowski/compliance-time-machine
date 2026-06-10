package com.aquacode.ctm.rules;

class DefaultRuleEngineTest extends RuleEngineContractTest {

    private final RuleEngine engine = new DefaultRuleEngine();

    @Override
    protected RuleEngine ruleEngine() {
        return engine;
    }

}
