package com.frauddetection.notification.service;

import com.frauddetection.common.audit.AuditLogger;
import com.frauddetection.common.dto.DecisionDTO;
import com.frauddetection.common.enums.AuditEventType;
import com.frauddetection.common.enums.DecisionType;
import com.frauddetection.common.enums.TransactionStatus;
import com.frauddetection.common.exceptions.ServiceException;
import com.frauddetection.common.exceptions.ValidationException;
import com.frauddetection.notification.entity.Notification;
import com.frauddetection.notification.enums.AlertStatus;
import com.frauddetection.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final WebSocketService webSocketService;
    private final EmailService emailService;
    private final AuditLogger auditLogger;
    private final TransactionStatusClient transactionStatusClient;

    @Transactional
    public void handleDecision(DecisionDTO decision) {
        if (decision.getStatus() == DecisionType.ACCEPTED) {
            log.debug("No alert needed for accepted transaction [{}]", decision.getTransactionId());
            return;
        }

        boolean critical = decision.getStatus() == DecisionType.BLOCKED;
        boolean emailSent = false;

        Notification notification = Notification.builder()
                .id(UUID.randomUUID().toString())
                .transactionId(decision.getTransactionId())
                .decisionStatus(decision.getStatus())
                .fusedScore(decision.getFusedScore())
                .alertStatus(AlertStatus.OPEN)
                .emailSent(false)
                .createdAt(Instant.now())
                .build();

        if (critical) {
            emailSent = emailService.sendCriticalAlertEmail(notification);
            notification.setEmailSent(emailSent);
        }

        notificationRepository.save(notification);
        webSocketService.broadcastAlert(notification);

        log.info("Alert created for transaction [{}] (status={}, emailSent={})",
                decision.getTransactionId(), decision.getStatus(), emailSent);
    }

    public List<Notification> listAlerts() {
        return notificationRepository.findAll();
    }

    @Transactional
    public Notification resolve(String alertId, AlertStatus newStatus, String reason, String username) {
        Notification notification = notificationRepository.findById(alertId)
                .orElseThrow(() -> new ServiceException("Alert not found: " + alertId));

        if (newStatus == AlertStatus.DISMISSED && (reason == null || reason.isBlank())) {
            throw new ValidationException("A reason is required to dismiss an alert");
        }

        notification.setAlertStatus(newStatus);
        notification.setResolvedBy(username);
        notification.setResolutionReason(reason);
        notification.setResolvedAt(Instant.now());

        if (newStatus == AlertStatus.DISMISSED) {
            transactionStatusClient.updateStatus(notification.getTransactionId(), TransactionStatus.ACCEPTED);
            notification.setDecisionStatus(DecisionType.ACCEPTED);
        } else if (newStatus == AlertStatus.VALIDATED && notification.getDecisionStatus() == DecisionType.MONITORED) {
            transactionStatusClient.updateStatus(notification.getTransactionId(), TransactionStatus.BLOCKED);
            notification.setDecisionStatus(DecisionType.BLOCKED);
        }

        notificationRepository.save(notification);

        auditLogger.log(AuditEventType.ALERT_RESOLVED, username, notification.getTransactionId(),
                "Alert " + alertId + " resolved as " + newStatus + (reason != null ? " (" + reason + ")" : ""));

        return notification;
    }
}
