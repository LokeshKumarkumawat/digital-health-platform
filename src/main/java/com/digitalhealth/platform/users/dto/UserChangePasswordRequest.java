package com.digitalhealth.platform.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserChangePasswordRequest {

    @NotBlank
    private String currentPassword;

    @NotBlank
    private String newPassword;
}

//Change password (logged-in user)