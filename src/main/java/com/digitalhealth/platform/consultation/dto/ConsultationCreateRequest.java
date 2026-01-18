package com.digitalhealth.platform.consultation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationCreateRequest {

    @NotNull
    private Long appointmentId;

    private OffsetDateTime consultationDate;

    private String subjectiveNotes;

    private String objectiveFindings;

    private String assessment;

    private String plan;
}

//Doctor submits consultation notes