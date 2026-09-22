package com.digitalhealth.platform.appointment.dto;

import jakarta.validation.constraints.Future;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentUpdateRequest {

    @Future
    private OffsetDateTime startTime;

    @Future
    private OffsetDateTime endTime;

    private String meetingLink;

    private String purposeOfConsultation;

    private String initialSymptoms;
}

//Reschedule appointment
//Update consultation purpose