package com.digitalhealth.platform.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResetPasswordRequest {

    @NotBlank
    private String resetCode;

    @NotBlank
    private String newPassword;
}

//Password reset (forgot password)