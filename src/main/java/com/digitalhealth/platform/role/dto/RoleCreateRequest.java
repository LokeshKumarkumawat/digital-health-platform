package com.digitalhealth.platform.role.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCreateRequest {

    @NotBlank
    private String name; // ROLE_ADMIN, ROLE_DOCTOR, ROLE_PATIENT
}

//Admin creates a new role
//System initializes default roles
//✔ Simple
//✔ Role names are immutable in meaning