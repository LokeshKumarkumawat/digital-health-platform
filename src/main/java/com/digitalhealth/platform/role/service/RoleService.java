package com.digitalhealth.platform.role.service;

import com.digitalhealth.platform.common.exception.ConflictException;
import com.digitalhealth.platform.common.exception.ResourceNotFoundException;
import com.digitalhealth.platform.role.dto.RoleCreateRequest;
import com.digitalhealth.platform.role.dto.RoleResponse;
import com.digitalhealth.platform.role.dto.UserRoleAssignRequest;
import com.digitalhealth.platform.role.entity.Role;
import com.digitalhealth.platform.role.mapper.RoleMapper;
import com.digitalhealth.platform.role.repository.RoleRepository;
import com.digitalhealth.platform.users.entity.User;
import com.digitalhealth.platform.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RoleMapper roleMapper;

    @Transactional
    public RoleResponse createRole(RoleCreateRequest request) {
        log.info("Creating role with name: {}", request.getName());

        if (roleRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Role with name " + request.getName() + " already exists");
        }

        Role role = Role.builder()
                .name(request.getName())
                .build();

        Role savedRole = roleRepository.save(role);
        log.info("Role created successfully with id: {}", savedRole.getId());

        return roleMapper.toResponse(savedRole);
    }

    public List<RoleResponse> getAllRoles() {
        log.debug("Fetching all roles");
        return roleRepository.findAll().stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

    public RoleResponse getRoleById(Long id) {
        log.debug("Fetching role with id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return roleMapper.toResponse(role);
    }

    @Transactional
    public void assignRoleToUser(UserRoleAssignRequest request) {
        log.info("Assigning role {} to user {}", request.getRoleId(), request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

        if (user.getRoles().contains(role)) {
            throw new ConflictException("User already has this role assigned");
        }

        user.getRoles().add(role);
        userRepository.save(user);

        log.info("Role {} assigned successfully to user {}", request.getRoleId(), request.getUserId());
    }

    @Transactional
    public void revokeRoleFromUser(UserRoleAssignRequest request) {
        log.info("Revoking role {} from user {}", request.getRoleId(), request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

        if (!user.getRoles().remove(role)) {
            throw new ResourceNotFoundException("User does not have this role assigned");
        }

        userRepository.save(user);

        log.info("Role {} revoked successfully from user {}", request.getRoleId(), request.getUserId());
    }

    public List<RoleResponse> getUserRoles(Long userId) {
        log.debug("Fetching roles for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return user.getRoles().stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());
    }

}