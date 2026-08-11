package com.frauddetection.audit.controller;

import com.frauddetection.audit.entity.AuditEvent;
import com.frauddetection.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<Page<AuditEvent>> search(
            @RequestParam(name = "correlationId", required = false) String correlationId,
            @RequestParam(name = "username", required = false) String username,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(auditService.search(correlationId, username, pageable));
    }
}
