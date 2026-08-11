package com.frauddetection.notification.dto;

import com.frauddetection.notification.enums.AlertStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResolveAlertRequest {

    @NotNull
    private AlertStatus status;

    private String reason;
}
