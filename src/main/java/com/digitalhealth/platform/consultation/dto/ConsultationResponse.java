package com.digitalhealth.platform.consultation.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationResponse {

    private Long id;

    private OffsetDateTime consultationDate;

    private String subjectiveNotes;
    private String objectiveFindings;
    private String assessment;
    private String plan;

    // Appointment reference
    private Long appointmentId;

    // Doctor summary
    private Long doctorId;
    private String doctorName;

    // Patient summary
    private Long patientId;
    private String patientName;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

//Returned to frontend / mobile app
