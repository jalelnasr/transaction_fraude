package com.frauddetection.notification.service;

import com.frauddetection.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketService {

    private static final String ALERTS_DESTINATION = "/topic/alerts";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastAlert(Notification notification) {
        try {
            messagingTemplate.convertAndSend(ALERTS_DESTINATION, notification);
            log.info("Broadcast alert for transaction [{}] to {}", notification.getTransactionId(), ALERTS_DESTINATION);
        } catch (Exception e) {
            log.error("Failed to broadcast alert for transaction [{}]", notification.getTransactionId(), e);
        }
    }
}
