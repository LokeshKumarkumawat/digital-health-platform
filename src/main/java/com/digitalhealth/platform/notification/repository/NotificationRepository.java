package com.digitalhealth.platform.notification.repository;

import com.digitalhealth.platform.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
