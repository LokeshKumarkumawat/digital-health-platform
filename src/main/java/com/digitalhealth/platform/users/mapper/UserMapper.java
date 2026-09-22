package com.digitalhealth.platform.users.mapper;

import com.digitalhealth.platform.role.entity.Role;
import com.digitalhealth.platform.users.dto.UserResponse;
import com.digitalhealth.platform.users.dto.UserSummaryResponse;
import com.digitalhealth.platform.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles")
    UserResponse toResponse(User user);

    UserSummaryResponse toSummary(User user);

    // MapStruct will auto-use this method
    default Set<String> map(List<Role> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }

        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}