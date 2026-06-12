package com.aquacode.ctm.rules.application;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EngineConfiguration {

    @Bean
    @ConditionalOnProperty(name = "ruleEngine.type", havingValue = "default", matchIfMissing = true)
    public DefaultRuleEngine defaultRuleEngine() {
        return new DefaultRuleEngine();
    }

    @Bean
    @ConditionalOnProperty(name = "ruleEngine.type", havingValue = "concurrent")
    public ConcurrentRuleEngine concurrentRuleEngine() {
        return new ConcurrentRuleEngine();
    }
}
