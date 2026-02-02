package com.digitalhealth.platform.doctor.controller;

import com.digitalhealth.platform.common.enums.Specialization;
import com.digitalhealth.platform.common.response.ApiResponse;
import com.digitalhealth.platform.doctor.dto.*;
import com.digitalhealth.platform.doctor.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Doctor Management
 *
 * Endpoints:
 * - POST   /api/v1/doctors                          - Create doctor profile (ADMIN or authenticated user)
 * - GET    /api/v1/doctors/me                       - Get current doctor profile (DOCTOR)
 * - PUT    /api/v1/doctors/me                       - Update current doctor profile (DOCTOR)
 * - GET    /api/v1/doctors/{id}                     - Get doctor by ID (authenticated users)
 * - PUT    /api/v1/doctors/{id}                     - Update doctor by ID (ADMIN)
 * - DELETE /api/v1/doctors/{id}                     - Delete doctor (ADMIN only)
 * - GET    /api/v1/doctors                          - Get all doctors full details (ADMIN only)
 * - GET    /api/v1/doctors/summary                  - Get all doctors summary (public)
 * - GET    /api/v1/doctors/user/{userId}            - Get doctor by user ID (authenticated users)
 * - GET    /api/v1/doctors/search/name              - Search doctors by name (public)
 * - GET    /api/v1/doctors/search/specialization    - Search by specialization (public)
 * - GET    /api/v1/doctors/enums/specializations    - Get specialization options (public)
 * - GET    /api/v1/doctors/{id}/verify-license      - Verify doctor license (ADMIN)
 * - GET    /api/v1/doctors/{id}/appointments/status - Check if doctor has appointments (ADMIN)
 * - GET    /api/v1/doctors/stats/specialization     - Get doctor count by specialization (ADMIN)
 */
@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    /**
     * Create a new doctor profile
     * ADMIN can create for any user, authenticated users can create for themselves
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or (isAuthenticated() and #request.userId == authentication.principal.id)")
    public ResponseEntity<ApiResponse<DoctorResponse>> createDoctor(
            @Valid @RequestBody DoctorCreateRequest request) {

        DoctorResponse response = doctorService.createDoctor(request);

        ApiResponse<DoctorResponse> apiResponse = ApiResponse.<DoctorResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Doctor profile created successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Get current authenticated doctor's profile
     * Only accessible by doctors themselves
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> getCurrentDoctorProfile() {

        DoctorResponse response = doctorService.getCurrentDoctorProfile();

        ApiResponse<DoctorResponse> apiResponse = ApiResponse.<DoctorResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor profile retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update current authenticated doctor's profile
     * Doctors can update their own information (except license number)
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateCurrentDoctorProfile(
            @Valid @RequestBody DoctorUpdateRequest request) {

        DoctorResponse response = doctorService.updateCurrentDoctorProfile(request);

        ApiResponse<DoctorResponse> apiResponse = ApiResponse.<DoctorResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor profile updated successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get doctor by ID
     * Accessible by authenticated users (for viewing doctor details when booking)
     */
    @GetMapping("/{doctorId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorById(
            @PathVariable Long doctorId) {

        DoctorResponse response = doctorService.getDoctorById(doctorId);

        ApiResponse<DoctorResponse> apiResponse = ApiResponse.<DoctorResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update doctor by ID
     * Used by admins to update doctor information
     */
    @PutMapping("/{doctorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<DoctorResponse>> updateDoctorById(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorUpdateRequest request) {

        DoctorResponse response = doctorService.updateDoctorById(doctorId, request);

        ApiResponse<DoctorResponse> apiResponse = ApiResponse.<DoctorResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor updated successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete doctor profile
     * Admin only - validates no active appointments exist
     */
    @DeleteMapping("/{doctorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable Long doctorId) {

        doctorService.deleteDoctor(doctorId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor deleted successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all doctors (full details)
     * Admin only - returns complete doctor information
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<DoctorResponse>>> getAllDoctors() {

        List<DoctorResponse> doctors = doctorService.getAllDoctors();

        ApiResponse<List<DoctorResponse>> apiResponse = ApiResponse.<List<DoctorResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All doctors retrieved successfully")
                .data(doctors)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all doctors summary
     * Public endpoint for appointment booking and doctor search
     * Returns limited information (no license numbers)
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<DoctorSummaryResponse>>> getAllDoctorsSummary() {

        List<DoctorSummaryResponse> doctors = doctorService.getAllDoctorsSummary();

        ApiResponse<List<DoctorSummaryResponse>> apiResponse = ApiResponse.<List<DoctorSummaryResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor summaries retrieved successfully")
                .data(doctors)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get doctor by user ID
     * Useful for linking user accounts to doctor profiles
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DoctorResponse>> getDoctorByUserId(
            @PathVariable Long userId) {

        DoctorResponse response = doctorService.getDoctorByUserId(userId);

        ApiResponse<DoctorResponse> apiResponse = ApiResponse.<DoctorResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Search doctors by name
     * Public endpoint for patients to find doctors
     */
    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<List<DoctorSummaryResponse>>> searchDoctorsByName(
            @RequestParam String query) {

        List<DoctorSummaryResponse> doctors = doctorService.searchDoctorsByName(query);

        ApiResponse<List<DoctorSummaryResponse>> apiResponse = ApiResponse.<List<DoctorSummaryResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Search completed successfully")
                .data(doctors)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Search doctors by specialization
     * Public endpoint for patients to find specialized doctors
     */
    @GetMapping("/search/specialization")
    public ResponseEntity<ApiResponse<List<DoctorSummaryResponse>>> searchDoctorsBySpecialization(
            @RequestParam Specialization specialization) {

        List<DoctorSummaryResponse> doctors = doctorService.searchDoctorsBySpecialization(specialization);

        ApiResponse<List<DoctorSummaryResponse>> apiResponse = ApiResponse.<List<DoctorSummaryResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctors retrieved successfully for specialization: " + specialization.name())
                .data(doctors)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all specialization enum values
     * Public endpoint for form dropdowns
     */
    @GetMapping("/enums/specializations")
    public ResponseEntity<ApiResponse<List<Specialization>>> getAllSpecializations() {

        List<Specialization> specializations = doctorService.getAllSpecializations();

        ApiResponse<List<Specialization>> apiResponse = ApiResponse.<List<Specialization>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Specializations retrieved successfully")
                .data(specializations)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Verify doctor license
     * Admin endpoint to check license validity
     */
    @GetMapping("/{doctorId}/verify-license")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> verifyDoctorLicense(
            @PathVariable Long doctorId) {

        boolean isValid = doctorService.verifyDoctorLicense(doctorId);

        ApiResponse<Boolean> apiResponse = ApiResponse.<Boolean>builder()
                .statusCode(HttpStatus.OK.value())
                .message(isValid ? "License is valid" : "License is invalid")
                .data(isValid)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Check if doctor has active appointments
     * Admin endpoint used before deletion or deactivation
     */
    @GetMapping("/{doctorId}/appointments/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Boolean>> checkDoctorAppointments(
            @PathVariable Long doctorId) {

        boolean hasAppointments = doctorService.hasActiveAppointments(doctorId);

        ApiResponse<Boolean> apiResponse = ApiResponse.<Boolean>builder()
                .statusCode(HttpStatus.OK.value())
                .message(hasAppointments ? "Doctor has active appointments" : "Doctor has no appointments")
                .data(hasAppointments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get doctor count by specialization
     * Admin endpoint for dashboard statistics
     */
    @GetMapping("/stats/specialization")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getDoctorCountBySpecialization(
            @RequestParam Specialization specialization) {

        long count = doctorService.getDoctorCountBySpecialization(specialization);

        ApiResponse<Long> apiResponse = ApiResponse.<Long>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor count retrieved for specialization: " + specialization.name())
                .data(count)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}