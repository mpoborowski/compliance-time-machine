package com.aquacode.ctm;

import org.springframework.boot.SpringApplication;

public class TestComplianceTimeMachineApplication {

    public static void main(String[] args) {
        SpringApplication.from(ComplianceTimeMachineApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
