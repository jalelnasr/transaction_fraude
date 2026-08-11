package com.frauddetection.notification.service;

import com.frauddetection.common.audit.AuditLogger;
import com.frauddetection.common.dto.DecisionDTO;
import com.frauddetection.common.enums.DecisionType;
import com.frauddetection.common.exceptions.ValidationException;
import com.frauddetection.notification.entity.Notification;
import com.frauddetection.notification.enums.AlertStatus;
import com.frauddetection.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTests {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private WebSocketService webSocketService;
    @Mock
    private EmailService emailService;
    @Mock
    private AuditLogger auditLogger;
    @Mock
    private TransactionStatusClient transactionStatusClient;

    private NotificationService notificationService() {
        return new NotificationService(notificationRepository, webSocketService, emailService, auditLogger, transactionStatusClient);
    }

    @Test
    void doesNotCreateAlertForAcceptedDecision() {
        NotificationService service = notificationService();

        DecisionDTO decision = DecisionDTO.builder()
                .transactionId("tx-1")
                .status(DecisionType.ACCEPTED)
                .fusedScore(0.1)
                .build();

        service.handleDecision(decision);

        verify(notificationRepository, never()).save(any());
        verify(webSocketService, never()).broadcastAlert(any());
    }

    @Test
    void createsAlertAndSendsEmailForBlockedDecision() {
        NotificationService service = notificationService();
        when(emailService.sendCriticalAlertEmail(any())).thenReturn(true);

        DecisionDTO decision = DecisionDTO.builder()
                .transactionId("tx-2")
                .status(DecisionType.BLOCKED)
                .fusedScore(0.9)
                .build();

        service.handleDecision(decision);

        verify(notificationRepository, times(1)).save(any());
        verify(webSocketService, times(1)).broadcastAlert(any());
        verify(emailService, times(1)).sendCriticalAlertEmail(any());
    }

    @Test
    void createsAlertWithoutEmailForMonitoredDecision() {
        NotificationService service = notificationService();

        DecisionDTO decision = DecisionDTO.builder()
                .transactionId("tx-3")
                .status(DecisionType.MONITORED)
                .fusedScore(0.6)
                .build();

        service.handleDecision(decision);

        verify(notificationRepository, times(1)).save(any());
        verify(emailService, never()).sendCriticalAlertEmail(any());
    }

    @Test
    void resolvingWithDismissedRequiresReason() {
        NotificationService service = notificationService();
        Notification notification = Notification.builder()
                .id("alert-1")
                .transactionId("tx-4")
                .alertStatus(AlertStatus.OPEN)
                .createdAt(Instant.now())
                .build();
        when(notificationRepository.findById("alert-1")).thenReturn(Optional.of(notification));

        assertThatThrownBy(() -> service.resolve("alert-1", AlertStatus.DISMISSED, null, "analyst1"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void resolvingWithValidatedDoesNotRequireReason() {
        NotificationService service = notificationService();
        Notification notification = Notification.builder()
                .id("alert-2")
                .transactionId("tx-5")
                .alertStatus(AlertStatus.OPEN)
                .createdAt(Instant.now())
                .build();
        when(notificationRepository.findById("alert-2")).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Notification resolved = service.resolve("alert-2", AlertStatus.VALIDATED, null, "analyst1");

        assertThat(resolved.getAlertStatus()).isEqualTo(AlertStatus.VALIDATED);
        assertThat(resolved.getResolvedBy()).isEqualTo("analyst1");
    }
}
