package com.digitalhealth.platform.users.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "password_reset_code")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_code_seq_gen")
    @SequenceGenerator(
            name = "password_reset_code_seq_gen",
            sequenceName = "password_reset_code_seq",
            allocationSize = 1
    )
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

    @Column(name = "expiry_date", nullable = false)
    private OffsetDateTime expiryDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
