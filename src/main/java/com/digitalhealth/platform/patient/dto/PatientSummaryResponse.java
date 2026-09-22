package com.digitalhealth.platform.patient.dto;

import com.digitalhealth.platform.common.enums.BloodGroup;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientSummaryResponse {

    private Long id;

    private String fullName;

    private Integer age;

    private BloodGroup bloodGroup;
}


//Appointment lists
//Doctor dashboard
//No contact info
//No sensitive notes
//Computed fields allowed