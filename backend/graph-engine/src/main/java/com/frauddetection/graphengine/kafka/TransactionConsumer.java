package com.frauddetection.graphengine.kafka;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.graphengine.service.GraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionConsumer {

    private static final String TRANSACTIONS_TOPIC = "transactions";

    private final GraphService graphService;

    @KafkaListener(
            topics = TRANSACTIONS_TOPIC,
            groupId = "graph-engine-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(TransactionDTO transaction) {
        log.info("Received transaction [{}] for graph update", transaction.getTransactionId());
        graphService.recordTransaction(transaction);
    }
}
