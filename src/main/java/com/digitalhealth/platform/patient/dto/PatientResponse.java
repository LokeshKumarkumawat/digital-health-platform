package com.digitalhealth.platform.patient.dto;

import com.digitalhealth.platform.common.enums.BloodGroup;
import com.digitalhealth.platform.common.enums.Genotype;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponse {

    private Long id;

    private String firstName;
    private String lastName;

    private LocalDate dateOfBirth;
    private Integer age;

    private String phone;

    private String knownAllergies;

    private BloodGroup bloodGroup;
    private Genotype genotype;

    private Long userId;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

//Patient self view
//Doctor consultation view
//Admin