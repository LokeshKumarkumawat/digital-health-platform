package com.digitalhealth.platform.appointment.entity;

import com.digitalhealth.platform.common.enums.AppointmentStatus;
import com.digitalhealth.platform.consultation.entity.Consultation;
import com.digitalhealth.platform.doctor.entity.Doctor;
import com.digitalhealth.platform.patient.entity.Patient;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appointments_seq_gen")
    @SequenceGenerator(name = "appointments_seq_gen", sequenceName = "appointments_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime startTime;

    private OffsetDateTime endTime;

    private String meetingLink;

    @Column(name = "purpose_of_consultation", columnDefinition = "TEXT")
    private String purposeOfConsultation;

    @Column(name = "initial_symptoms", columnDefinition = "TEXT")
    private String initialSymptoms;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToOne(mappedBy = "appointment", fetch = FetchType.LAZY)
    private Consultation consultation;

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
