package com.frauddetection.common.audit;

import com.frauddetection.common.enums.AuditEventType;

public interface AuditLogger {

    void log(AuditEventType eventType, String username, String correlationId, String details);
}
