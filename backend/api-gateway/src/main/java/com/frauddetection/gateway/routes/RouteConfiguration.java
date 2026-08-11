package com.frauddetection.gateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfiguration {

    @Value("${services.transaction-service.url}")
    private String transactionServiceUrl;

    @Value("${services.rule-engine.url}")
    private String ruleEngineUrl;

    @Value("${services.ml-engine.url}")
    private String mlEngineUrl;

    @Value("${services.fraud-decision-engine.url}")
    private String fraudDecisionEngineUrl;

    @Value("${services.explainability-engine.url}")
    private String explainabilityEngineUrl;

    @Value("${services.notification-service.url}")
    private String notificationServiceUrl;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Routes les plus specifiques en premier
                .route("fraud-decision-engine", r -> r
                        .path("/api/transactions/*/decision")
                        .uri(fraudDecisionEngineUrl))
                .route("explainability-engine", r -> r
                        .path("/api/transactions/*/explanation")
                        .uri(explainabilityEngineUrl))
                .route("transaction-service", r -> r
                        .path("/api/transactions/**")
                        .uri(transactionServiceUrl))
                .route("rule-engine", r -> r
                        .path("/api/rules/**")
                        .uri(ruleEngineUrl))
                .route("ml-engine", r -> r
                        .path("/api/models/**")
                        .uri(mlEngineUrl))
                .route("notification-service", r -> r
                        .path("/api/alerts/**")
                        .uri(notificationServiceUrl))
                .build();
    }
}
