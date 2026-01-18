package com.digitalhealth.platform.doctor.dto;

import com.digitalhealth.platform.common.enums.Specialization;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorSummaryResponse {

    private Long id;

    private String fullName;

    private Specialization specialization;
}

//Appointment booking
//Doctor search results
//PUBLIC VIEW