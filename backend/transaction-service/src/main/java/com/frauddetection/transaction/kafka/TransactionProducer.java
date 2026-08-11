package com.frauddetection.transaction.kafka;

import com.frauddetection.common.dto.TransactionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(TransactionDTO transactionDTO) {
        String transactionId = transactionDTO.getTransactionId();
        kafkaTemplate.send(KafkaTopicConfig.TRANSACTIONS_TOPIC, transactionId, transactionDTO)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish transaction [{}] to Kafka", transactionId, ex);
                    } else {
                        log.info("Published transaction [{}] to topic [{}]", transactionId,
                                KafkaTopicConfig.TRANSACTIONS_TOPIC);
                    }
                });
    }
}
