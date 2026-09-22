package com.digitalhealth.platform.notification.dto;

import com.digitalhealth.platform.common.enums.NotificationType;
import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;

    private String subject;
    private String message;

    private NotificationType type;

    private boolean read;

    private OffsetDateTime createdAt;
}

//Returned to frontend
//✔ No recipient leakage
//✔ No userId exposure
//✔ Frontend-friendly