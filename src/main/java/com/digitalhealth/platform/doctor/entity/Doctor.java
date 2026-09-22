package com.digitalhealth.platform.doctor.entity;

import com.digitalhealth.platform.appointment.entity.Appointment;
import com.digitalhealth.platform.common.enums.Specialization;
import com.digitalhealth.platform.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "doctors_seq_gen")
    @SequenceGenerator(name = "doctors_seq_gen", sequenceName = "doctors_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Specialization specialization;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @Version
    private Long version;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}

