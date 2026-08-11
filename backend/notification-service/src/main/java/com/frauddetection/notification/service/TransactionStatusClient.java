package com.frauddetection.notification.service;

import com.frauddetection.common.enums.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionStatusClient {

    private final RestClient transactionServiceRestClient;

    public void updateStatus(String transactionId, TransactionStatus newStatus) {
        try {
            transactionServiceRestClient.patch()
                    .uri("/api/transactions/{id}/status", transactionId)
                    .body(Map.of("status", newStatus.name()))
                    .retrieve()
                    .toBodilessEntity();
            log.info("Transaction [{}] status updated to [{}] following analyst resolution", transactionId, newStatus);
        } catch (Exception e) {
            log.error("Failed to update transaction [{}] status to [{}], continuing without blocking alert resolution",
                    transactionId, newStatus, e);
        }
    }
}
