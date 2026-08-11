package com.frauddetection.audit.kafka;

import com.frauddetection.audit.service.AuditService;
import com.frauddetection.common.dto.AuditEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditService auditService;

    @KafkaListener(
            topics = KafkaTopicConfig.AUDIT_EVENTS_TOPIC,
            groupId = "audit-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(AuditEventDTO auditEventDTO) {
        log.info("Received audit event [{}] from user [{}]", auditEventDTO.getEventType(), auditEventDTO.getUsername());
        auditService.store(auditEventDTO);
    }
}
