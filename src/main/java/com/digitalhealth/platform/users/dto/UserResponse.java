package com.digitalhealth.platform.users.dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String name;

    private String email;

    private String authProvider;

    private Set<String> roles;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

//ADMIN VIEW
//✔ No password
//✔ Role names only
//✔ Safe admin view