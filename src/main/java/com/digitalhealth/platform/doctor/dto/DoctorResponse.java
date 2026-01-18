package com.digitalhealth.platform.doctor.dto;

import com.digitalhealth.platform.common.enums.Specialization;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {

    private Long id;

    private String firstName;
    private String lastName;

    private Specialization specialization;

    private String licenseNumber;

    private Long userId;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

//Admin
//Doctor dashboard