package com.frauddetection.notification.service;

import com.frauddetection.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.email.from:alerts@frauddetection.local}")
    private String fromAddress;

    @Value("${notification.email.to:fraud-team@frauddetection.local}")
    private String toAddress;

    public boolean sendCriticalAlertEmail(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toAddress);
            message.setSubject("[ALERTE CRITIQUE] Transaction bloquee " + notification.getTransactionId());
            message.setText(String.format(
                    "Une transaction a ete bloquee automatiquement.%n"
                            + "TransactionId: %s%n"
                            + "Score fusionne: %.2f%n"
                            + "Merci de consulter le dashboard pour plus de details.",
                    notification.getTransactionId(), notification.getFusedScore()));

            mailSender.send(message);
            log.info("Sent critical alert email for transaction [{}]", notification.getTransactionId());
            return true;
        } catch (Exception e) {
            log.error("Failed to send alert email for transaction [{}], continuing without blocking the decision",
                    notification.getTransactionId(), e);
            return false;
        }
    }
}
