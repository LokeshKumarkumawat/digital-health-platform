package com.digitalhealth.platform.consultation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultationUpdateRequest {

    private String subjectiveNotes;

    private String objectiveFindings;

    private String assessment;

    private String plan;
}

// Doctor edits notes
// Adds assessment later
// Partial updates allowed
// No appointment change allowed