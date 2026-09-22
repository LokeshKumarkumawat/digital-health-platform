package com.digitalhealth.platform.patient.entity;

import com.digitalhealth.platform.appointment.entity.Appointment;
import com.digitalhealth.platform.common.enums.BloodGroup;
import com.digitalhealth.platform.common.enums.Genotype;
import com.digitalhealth.platform.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patients_seq_gen")
    @SequenceGenerator(name = "patients_seq_gen", sequenceName = "patients_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String phone;

    @Column(name = "known_allergies", columnDefinition = "TEXT")
    private String knownAllergies;

    @Enumerated(EnumType.STRING)
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    private Genotype genotype;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
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

