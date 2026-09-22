package com.digitalhealth.platform.doctor.dto;

import com.digitalhealth.platform.common.enums.Specialization;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorCreateRequest {

    @NotNull
    private Long userId;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private Specialization specialization;

    @NotNull
    private String licenseNumber;
}

//Admin onboards a doctor
//Doctor completes profile after signup
//✔ userId links to existing user
//✔ License mandatory (healthcare compliance)