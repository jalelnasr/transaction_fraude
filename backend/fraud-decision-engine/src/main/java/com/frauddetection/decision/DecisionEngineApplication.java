package com.frauddetection.decision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.frauddetection.decision", "com.frauddetection.common"})
public class DecisionEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(DecisionEngineApplication.class, args);
    }
}
