package com.digitalhealth.platform.appointment.dto;

import com.digitalhealth.platform.common.enums.AppointmentStatus;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {

    private Long id;

    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    private String meetingLink;

    private String purposeOfConsultation;
    private String initialSymptoms;

    private AppointmentStatus status;

    // Doctor summary
    private Long doctorId;
    private String doctorName;
    private String specialization;

    // Patient summary
    private Long patientId;
    private String patientName;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
// This is what frontend sees