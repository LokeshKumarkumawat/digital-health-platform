package com.digitalhealth.platform.notification.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationReadRequest {
    private boolean read;
}

//User opens notification
//Mobile app marks read