package com.digitalhealth.platform.patient.controller;

import com.digitalhealth.platform.common.enums.BloodGroup;
import com.digitalhealth.platform.common.enums.Genotype;
import com.digitalhealth.platform.common.response.ApiResponse;
import com.digitalhealth.platform.patient.dto.*;
import com.digitalhealth.platform.patient.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Patient Management
 *
 * Endpoints:
 * - POST   /api/v1/patients                     - Create patient profile (ADMIN or authenticated user)
 * - GET    /api/v1/patients/me                  - Get current patient profile (PATIENT)
 * - PUT    /api/v1/patients/me                  - Update current patient profile (PATIENT)
 * - GET    /api/v1/patients/{id}                - Get patient by ID (ADMIN, DOCTOR, or owner)
 * - PUT    /api/v1/patients/{id}                - Update patient by ID (ADMIN, DOCTOR)
 * - DELETE /api/v1/patients/{id}                - Delete patient (ADMIN only)
 * - GET    /api/v1/patients                     - Get all patients full details (ADMIN only)
 * - GET    /api/v1/patients/summary             - Get all patients summary (ADMIN, DOCTOR)
 * - GET    /api/v1/patients/user/{userId}       - Get patient by user ID (ADMIN, DOCTOR, or owner)
 * - GET    /api/v1/patients/search              - Search patients by name (ADMIN, DOCTOR)
 * - GET    /api/v1/patients/enums/blood-groups  - Get blood group options
 * - GET    /api/v1/patients/enums/genotypes     - Get genotype options
 */
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    /**
     * Create a new patient profile
     * ADMIN can create for any user, authenticated users can create for themselves
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or (isAuthenticated() and #request.userId == authentication.principal.id)")
    public ResponseEntity<ApiResponse<PatientResponse>> createPatient(
            @Valid @RequestBody PatientCreateRequest request) {

        PatientResponse response = patientService.createPatient(request);

        ApiResponse<PatientResponse> apiResponse = ApiResponse.<PatientResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Patient profile created successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Get current authenticated patient's profile
     * Only accessible by patients themselves
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponse>> getCurrentPatientProfile() {

        PatientResponse response = patientService.getCurrentPatientProfile();

        ApiResponse<PatientResponse> apiResponse = ApiResponse.<PatientResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient profile retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update current authenticated patient's profile
     * Patients can update their own information
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<ApiResponse<PatientResponse>> updateCurrentPatientProfile(
            @Valid @RequestBody PatientUpdateRequest request) {

        PatientResponse response = patientService.updateCurrentPatientProfile(request);

        ApiResponse<PatientResponse> apiResponse = ApiResponse.<PatientResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient profile updated successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get patient by ID
     * Accessible by ADMIN, DOCTOR, or the patient themselves
     */
    @GetMapping("/{patientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or " +
            "@patientService.getPatientById(#patientId).userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientById(
            @PathVariable Long patientId) {

        PatientResponse response = patientService.getPatientById(patientId);

        ApiResponse<PatientResponse> apiResponse = ApiResponse.<PatientResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update patient by ID
     * Used by doctors or admins to update patient medical information
     */
    @PutMapping("/{patientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<PatientResponse>> updatePatientById(
            @PathVariable Long patientId,
            @Valid @RequestBody PatientUpdateRequest request) {

        PatientResponse response = patientService.updatePatientById(patientId, request);

        ApiResponse<PatientResponse> apiResponse = ApiResponse.<PatientResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient updated successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete patient profile
     * Admin only - validates no active appointments exist
     */
    @DeleteMapping("/{patientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long patientId) {

        patientService.deletePatient(patientId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient deleted successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all patients (full details)
     * Admin only - returns complete patient information
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<PatientResponse>>> getAllPatients() {

        List<PatientResponse> patients = patientService.getAllPatients();

        ApiResponse<List<PatientResponse>> apiResponse = ApiResponse.<List<PatientResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All patients retrieved successfully")
                .data(patients)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all patients summary
     * For admin dashboard and doctor's patient lists
     * Returns limited information (no sensitive data)
     */
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<List<PatientSummaryResponse>>> getAllPatientsSummary() {

        List<PatientSummaryResponse> patients = patientService.getAllPatientsSummary();

        ApiResponse<List<PatientSummaryResponse>> apiResponse = ApiResponse.<List<PatientSummaryResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient summaries retrieved successfully")
                .data(patients)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get patient by user ID
     * Useful for linking user accounts to patient profiles
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<PatientResponse>> getPatientByUserId(
            @PathVariable Long userId) {

        PatientResponse response = patientService.getPatientByUserId(userId);

        ApiResponse<PatientResponse> apiResponse = ApiResponse.<PatientResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Search patients by name
     * For doctor's search functionality
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<List<PatientSummaryResponse>>> searchPatients(
            @RequestParam String query) {

        List<PatientSummaryResponse> patients = patientService.searchPatientsByName(query);

        ApiResponse<List<PatientSummaryResponse>> apiResponse = ApiResponse.<List<PatientSummaryResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Search completed successfully")
                .data(patients)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all blood group enum values
     * Public endpoint for form dropdowns
     */
    @GetMapping("/enums/blood-groups")
    public ResponseEntity<ApiResponse<List<BloodGroup>>> getAllBloodGroups() {

        List<BloodGroup> bloodGroups = patientService.getAllBloodGroups();

        ApiResponse<List<BloodGroup>> apiResponse = ApiResponse.<List<BloodGroup>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Blood groups retrieved successfully")
                .data(bloodGroups)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all genotype enum values
     * Public endpoint for form dropdowns
     */
    @GetMapping("/enums/genotypes")
    public ResponseEntity<ApiResponse<List<Genotype>>> getAllGenotypes() {

        List<Genotype> genotypes = patientService.getAllGenotypes();

        ApiResponse<List<Genotype>> apiResponse = ApiResponse.<List<Genotype>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Genotypes retrieved successfully")
                .data(genotypes)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Calculate patient age
     * Utility endpoint for quick age calculation
     */
    @GetMapping("/{patientId}/age")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<Integer>> calculatePatientAge(
            @PathVariable Long patientId) {

        Integer age = patientService.calculateAge(patientId);

        ApiResponse<Integer> apiResponse = ApiResponse.<Integer>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Age calculated successfully")
                .data(age)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}