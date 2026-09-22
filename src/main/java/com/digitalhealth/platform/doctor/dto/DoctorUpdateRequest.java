package com.digitalhealth.platform.doctor.dto;

import com.digitalhealth.platform.common.enums.Specialization;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorUpdateRequest {

    private String firstName;

    private String lastName;

    private Specialization specialization;
}

// Doctor updates profile info
// Admin corrects data
// License not updatable (business rule)
// Prevents identity fraud