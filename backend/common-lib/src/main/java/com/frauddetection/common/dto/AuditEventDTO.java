package com.frauddetection.common.dto;

import com.frauddetection.common.enums.AuditEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventDTO {

    private AuditEventType eventType;
    private String username;
    private String correlationId;
    private String details;
    private Instant occurredAt;
}
