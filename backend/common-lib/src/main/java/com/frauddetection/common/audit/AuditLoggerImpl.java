package com.frauddetection.common.audit;

import com.frauddetection.common.dto.AuditEventDTO;
import com.frauddetection.common.enums.AuditEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLoggerImpl implements AuditLogger {

    private static final String AUDIT_TOPIC = "audit-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void log(AuditEventType eventType, String username, String correlationId, String details) {
        AuditEventDTO event = AuditEventDTO.builder()
                .eventType(eventType)
                .username(username)
                .correlationId(correlationId)
                .details(details)
                .occurredAt(Instant.now())
                .build();

        try {
            kafkaTemplate.send(AUDIT_TOPIC, correlationId, event);
        } catch (Exception e) {
            log.error("Failed to publish audit event [{}] for correlationId [{}]", eventType, correlationId, e);
        }
    }
}
