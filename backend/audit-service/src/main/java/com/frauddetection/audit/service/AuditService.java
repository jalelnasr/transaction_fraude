package com.frauddetection.audit.service;

import com.frauddetection.audit.entity.AuditEvent;
import com.frauddetection.audit.repository.AuditEventRepository;
import com.frauddetection.common.dto.AuditEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public void store(AuditEventDTO dto) {
        AuditEvent event = AuditEvent.builder()
                .eventType(dto.getEventType())
                .username(dto.getUsername())
                .correlationId(dto.getCorrelationId())
                .details(dto.getDetails())
                .occurredAt(dto.getOccurredAt())
                .receivedAt(Instant.now())
                .build();
        auditEventRepository.save(event);
    }

    public Page<AuditEvent> search(String correlationId, String username, Pageable pageable) {
        if (correlationId != null && !correlationId.isBlank()) {
            return auditEventRepository.findByCorrelationId(correlationId, pageable);
        }
        if (username != null && !username.isBlank()) {
            return auditEventRepository.findByUsername(username, pageable);
        }
        return auditEventRepository.findAll(pageable);
    }
}
