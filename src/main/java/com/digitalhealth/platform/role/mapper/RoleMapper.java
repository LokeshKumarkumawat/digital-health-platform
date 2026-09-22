package com.digitalhealth.platform.role.mapper;

import com.digitalhealth.platform.role.dto.RoleResponse;
import com.digitalhealth.platform.role.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toResponse(Role role);
}

