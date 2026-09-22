package com.digitalhealth.platform.role.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleAssignRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long roleId;
}

//Admin assigns roles to users