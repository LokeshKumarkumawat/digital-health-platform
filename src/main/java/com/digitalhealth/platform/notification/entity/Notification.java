package com.digitalhealth.platform.notification.entity;

import com.digitalhealth.platform.common.enums.NotificationType;
import com.digitalhealth.platform.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notifications_seq_gen")
    @SequenceGenerator(name = "notifications_seq_gen", sequenceName = "notifications_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    private String subject;
    private String recipient;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
