package com.digitalhealth.platform.role.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {

    private Long id;

    private String name;
}

//Returned to admin UI / system services