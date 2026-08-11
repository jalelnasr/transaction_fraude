package com.frauddetection.audit.service;

import com.frauddetection.audit.entity.AuditEvent;
import com.frauddetection.audit.repository.AuditEventRepository;
import com.frauddetection.common.dto.AuditEventDTO;
import com.frauddetection.common.enums.AuditEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuditServiceTests {

    @Mock
    private AuditEventRepository auditEventRepository;

    @Test
    void storesIncomingAuditEventWithReceivedTimestamp() {
        AuditService service = new AuditService(auditEventRepository);

        AuditEventDTO dto = AuditEventDTO.builder()
                .eventType(AuditEventType.RULE_CREATED)
                .username("admin1")
                .correlationId("rule-1")
                .details("Rule created: Test")
                .occurredAt(Instant.now())
                .build();

        service.store(dto);

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository).save(captor.capture());

        AuditEvent saved = captor.getValue();
        assertThat(saved.getEventType()).isEqualTo(AuditEventType.RULE_CREATED);
        assertThat(saved.getUsername()).isEqualTo("admin1");
        assertThat(saved.getReceivedAt()).isNotNull();
    }
}
