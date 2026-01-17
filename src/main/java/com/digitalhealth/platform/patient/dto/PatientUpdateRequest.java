package com.digitalhealth.platform.patient.dto;

import com.digitalhealth.platform.common.enums.BloodGroup;
import com.digitalhealth.platform.common.enums.Genotype;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientUpdateRequest {

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private String phone;

    private String knownAllergies;

    private BloodGroup bloodGroup;

    private Genotype genotype;
}

//Patient updates profile
//Doctor updates medical info (if allowed)
//Partial updates
//Safe & flexible