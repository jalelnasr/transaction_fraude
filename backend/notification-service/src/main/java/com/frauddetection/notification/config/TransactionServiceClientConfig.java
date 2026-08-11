package com.frauddetection.notification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class TransactionServiceClientConfig {

    @Value("${services.transaction-service.url}")
    private String transactionServiceUrl;

    @Bean
    public RestClient transactionServiceRestClient() {
        return RestClient.builder()
                .baseUrl(transactionServiceUrl)
                .build();
    }
}
