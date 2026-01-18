package com.digitalhealth.platform.appointment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentCreateRequest {

    @NotNull
    @Future
    private OffsetDateTime startTime;

    @Future
    private OffsetDateTime endTime;

    private String purposeOfConsultation;

    private String initialSymptoms;

    @NotNull
    private Long doctorId;

    @NotNull
    private Long patientId;
}

// Patient books an appointment
// Admin schedules appointment