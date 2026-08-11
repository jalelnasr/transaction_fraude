package com.frauddetection.audit.repository;

import com.frauddetection.audit.entity.AuditEvent;
import com.frauddetection.common.enums.AuditEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, String> {

    Page<AuditEvent> findByCorrelationId(String correlationId, Pageable pageable);

    Page<AuditEvent> findByEventType(AuditEventType eventType, Pageable pageable);

    Page<AuditEvent> findByUsername(String username, Pageable pageable);
}
