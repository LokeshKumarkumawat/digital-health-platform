package com.digitalhealth.platform.patient.dto;

import com.digitalhealth.platform.common.enums.BloodGroup;
import com.digitalhealth.platform.common.enums.Genotype;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientCreateRequest {

    @NotNull
    private Long userId;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private LocalDate dateOfBirth;

    private String phone;

    private String knownAllergies;

    private BloodGroup bloodGroup;

    private Genotype genotype;
}

// Patient completes onboarding
// Admin registers patient
// No ID
// User already exists
// Medical info optional (can be updated later)