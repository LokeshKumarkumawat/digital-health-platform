package com.digitalhealth.platform.notification.dto;

import com.digitalhealth.platform.common.enums.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCreateRequest {

    @NotNull
    private Long userId;

    @NotNull
    private NotificationType type; // EMAIL, SMS, PUSH

    @NotNull
    private String subject;

    @NotNull
    private String recipient;

    @NotNull
    private String message;
}

//Appointment created
//Consultation completed
//Password reset triggered
//✔ Used internally
//✔ Not exposed to end-users
//✔ Supports async processing (Kafka / MQ)