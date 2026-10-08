package com.digitalhealth.platform.users.mapper;

import com.digitalhealth.platform.doctor.repository.DoctorRepository;
import com.digitalhealth.platform.patient.repository.PatientRepository;
import com.digitalhealth.platform.role.entity.Role;
import com.digitalhealth.platform.users.dto.UserResponse;
import com.digitalhealth.platform.users.dto.UserSummaryResponse;
import com.digitalhealth.platform.users.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected PatientRepository patientRepository;

    @Autowired
    protected DoctorRepository doctorRepository;

    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "hasProfileComplete", source = ".", qualifiedByName = "computeProfileComplete")
    public abstract UserResponse toResponse(User user);

    public abstract UserSummaryResponse toSummary(User user);

    // Computed field - always fresh from DB
    @Named("computeProfileComplete")
    protected Boolean computeProfileComplete(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }

        // Admin always considered complete
        boolean isAdmin = user.getRoles() != null && user.getRoles().stream()
                .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));

        if (isAdmin) {
            return true;
        }

        boolean hasPatient = patientRepository.existsByUserId(user.getId());
        boolean hasDoctor = doctorRepository.existsByUserId(user.getId());

        return hasPatient || hasDoctor;
    }

    // Existing role mapper
    protected Set<String> map(List<Role> roles) {
        if (roles == null) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}