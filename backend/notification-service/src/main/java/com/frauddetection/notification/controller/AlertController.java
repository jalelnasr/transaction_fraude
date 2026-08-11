package com.frauddetection.notification.controller;

import com.frauddetection.notification.dto.ResolveAlertRequest;
import com.frauddetection.notification.entity.Notification;
import com.frauddetection.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> listAlerts() {
        return ResponseEntity.ok(notificationService.listAlerts());
    }

    @PostMapping("/{id}/resolve")
    public ResponseEntity<Notification> resolve(@PathVariable("id") String id,
                                                  @Valid @RequestBody ResolveAlertRequest request,
                                                  @RequestHeader(value = "X-User", defaultValue = "system") String username) {
        Notification updated = notificationService.resolve(id, request.getStatus(), request.getReason(), username);
        return ResponseEntity.ok(updated);
    }
}
