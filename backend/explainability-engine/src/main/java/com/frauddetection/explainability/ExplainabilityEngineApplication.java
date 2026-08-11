package com.frauddetection.explainability;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.frauddetection.explainability", "com.frauddetection.common"})
public class ExplainabilityEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExplainabilityEngineApplication.class, args);
    }
}
