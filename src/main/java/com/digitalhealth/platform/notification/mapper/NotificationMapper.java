package com.digitalhealth.platform.notification.mapper;

import com.digitalhealth.platform.notification.dto.NotificationResponse;
import com.digitalhealth.platform.notification.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);
}

