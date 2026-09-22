package com.digitalhealth.platform.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResetPasswordRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String resetCode;

    @NotBlank
    private String newPassword;
}

//Password reset (forgot password)