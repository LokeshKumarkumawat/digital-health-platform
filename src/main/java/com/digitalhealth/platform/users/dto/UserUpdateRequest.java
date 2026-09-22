package com.digitalhealth.platform.users.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {

    private String name;

    private String profilePictureUrl;
}

//User updates profile info
//No email change
//No password change here