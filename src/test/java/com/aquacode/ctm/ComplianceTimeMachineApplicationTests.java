package com.aquacode.ctm;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class ComplianceTimeMachineApplicationTests {

    @Test
    void contextLoads() {
        var modules = ApplicationModules.of(ComplianceTimeMachineApplication.class);
        modules.forEach(m -> System.out.println("Module: " + m.getDisplayName() + ":" + m.getBasePackage()));
        modules.verify();
    }

}
