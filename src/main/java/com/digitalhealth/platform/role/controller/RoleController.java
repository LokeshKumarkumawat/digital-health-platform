package com.digitalhealth.platform.role.controller;

import com.digitalhealth.platform.common.response.ApiResponse;
import com.digitalhealth.platform.role.dto.RoleCreateRequest;
import com.digitalhealth.platform.role.dto.RoleResponse;
import com.digitalhealth.platform.role.dto.UserRoleAssignRequest;
import com.digitalhealth.platform.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleCreateRequest request) {
        RoleResponse response = roleService.createRole(request);

        ApiResponse<RoleResponse> apiResponse = ApiResponse.<RoleResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Role created successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();

        ApiResponse<List<RoleResponse>> apiResponse = ApiResponse.<List<RoleResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Roles retrieved successfully")
                .data(roles)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse response = roleService.getRoleById(id);

        ApiResponse<RoleResponse> apiResponse = ApiResponse.<RoleResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<Void>> assignRoleToUser(@Valid @RequestBody UserRoleAssignRequest request) {
        roleService.assignRoleToUser(request);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role assigned to user successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/revoke")
    public ResponseEntity<ApiResponse<Void>> revokeRoleFromUser(@Valid @RequestBody UserRoleAssignRequest request) {
        roleService.revokeRoleFromUser(request);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role revoked from user successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getUserRoles(@PathVariable Long userId) {
        List<RoleResponse> roles = roleService.getUserRoles(userId);

        ApiResponse<List<RoleResponse>> apiResponse = ApiResponse.<List<RoleResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User roles retrieved successfully")
                .data(roles)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

}