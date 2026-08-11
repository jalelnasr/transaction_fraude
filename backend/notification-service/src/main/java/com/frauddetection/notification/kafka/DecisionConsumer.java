package com.frauddetection.notification.kafka;

import com.frauddetection.common.dto.DecisionDTO;
import com.frauddetection.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DecisionConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaTopicConfig.DECISIONS_TOPIC,
            groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(DecisionDTO decisionDTO) {
        log.info("Received decision [{}] for transaction [{}]",
                decisionDTO.getStatus(), decisionDTO.getTransactionId());
        notificationService.handleDecision(decisionDTO);
    }
}
