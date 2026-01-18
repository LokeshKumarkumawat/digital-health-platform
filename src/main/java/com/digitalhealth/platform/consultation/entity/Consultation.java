package com.digitalhealth.platform.consultation.entity;

import com.digitalhealth.platform.appointment.entity.Appointment;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "consultations")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consultations_seq_gen")
    @SequenceGenerator(name = "consultations_seq_gen", sequenceName = "consultations_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    private OffsetDateTime consultationDate;

    @Column(name = "subjective_notes", columnDefinition = "TEXT")
    private String subjectiveNotes;

    @Column(name = "objective_findings", columnDefinition = "TEXT")
    private String objectiveFindings;

    @Column(name = "assessment", columnDefinition = "TEXT")
    private String assessment;

    @Column(name = "plan", columnDefinition = "TEXT")
    private String plan;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

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
