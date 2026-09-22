package com.digitalhealth.platform.consultation.controller;

import com.digitalhealth.platform.common.response.ApiResponse;
import com.digitalhealth.platform.consultation.dto.*;
import com.digitalhealth.platform.consultation.service.ConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * REST Controller for Consultation Management
 *
 * Endpoints:
 * - POST   /api/v1/consultations                    - Create consultation notes (DOCTOR)
 * - GET    /api/v1/consultations/{id}               - Get consultation by ID
 * - PUT    /api/v1/consultations/{id}               - Update consultation notes (DOCTOR)
 * - DELETE /api/v1/consultations/{id}               - Delete consultation (ADMIN)
 * - GET    /api/v1/consultations                    - Get all consultations (ADMIN)
 * - GET    /api/v1/consultations/appointment/{id}   - Get consultation by appointment ID
 * - GET    /api/v1/consultations/patient/{id}       - Get patient consultation history
 * - GET    /api/v1/consultations/my-history         - Get current patient's history
 * - GET    /api/v1/consultations/doctor/{id}        - Get consultations by doctor
 * - GET    /api/v1/consultations/my-consultations   - Get current doctor's consultations
 * - GET    /api/v1/consultations/search             - Search consultations by keyword
 * - GET    /api/v1/consultations/recent             - Get recent consultations
 * - GET    /api/v1/consultations/date-range         - Get consultations by date range
 */
@RestController
@RequestMapping("/api/v1/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    private final ConsultationService consultationService;

    /**
     * Create consultation notes for a completed appointment
     * Only the assigned doctor can create notes
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<ConsultationResponse>> createConsultation(
            @Valid @RequestBody ConsultationCreateRequest request) {

        ConsultationResponse response = consultationService.createConsultation(request);

        ApiResponse<ConsultationResponse> apiResponse = ApiResponse.<ConsultationResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Consultation notes created successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Get consultation by ID
     * Accessible by patient, doctor, or admin involved
     */
    @GetMapping("/{consultationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ConsultationResponse>> getConsultationById(
            @PathVariable Long consultationId) {

        ConsultationResponse response = consultationService.getConsultationById(consultationId);

        ApiResponse<ConsultationResponse> apiResponse = ApiResponse.<ConsultationResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get consultation by appointment ID
     * Accessible by patient, doctor, or admin
     */
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ConsultationResponse>> getConsultationByAppointmentId(
            @PathVariable Long appointmentId) {

        ConsultationResponse response = consultationService.getConsultationByAppointmentId(appointmentId);

        ApiResponse<ConsultationResponse> apiResponse = ApiResponse.<ConsultationResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all consultations
     * Admin only
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getAllConsultations() {

        List<ConsultationResponse> consultations = consultationService.getAllConsultations();

        ApiResponse<List<ConsultationResponse>> apiResponse = ApiResponse.<List<ConsultationResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All consultations retrieved successfully")
                .data(consultations)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get consultation history for a patient
     * If patientId not provided, returns history for current authenticated patient
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getConsultationHistoryForPatient(
            @PathVariable Long patientId) {

        List<ConsultationResponse> consultations = consultationService.getConsultationHistoryForPatient(patientId);

        ApiResponse<List<ConsultationResponse>> apiResponse = ApiResponse.<List<ConsultationResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation history retrieved successfully")
                .data(consultations)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get current patient's consultation history
     * Patient only - returns their own consultation history
     */
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getMyConsultationHistory() {

        List<ConsultationResponse> consultations = consultationService.getMyConsultationHistory();

        ApiResponse<List<ConsultationResponse>> apiResponse = ApiResponse.<List<ConsultationResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your consultation history retrieved successfully")
                .data(consultations)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get consultations by doctor ID
     * Returns all consultations created by a specific doctor
     */
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getConsultationsByDoctorId(
            @PathVariable Long doctorId) {

        List<ConsultationResponse> consultations = consultationService.getConsultationsByDoctorId(doctorId);

        ApiResponse<List<ConsultationResponse>> apiResponse = ApiResponse.<List<ConsultationResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor consultations retrieved successfully")
                .data(consultations)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get current doctor's consultations
     * Doctor only - returns consultations they've created
     */
    @GetMapping("/my-consultations")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getMyConsultations() {

        List<ConsultationResponse> consultations = consultationService.getMyConsultations();

        ApiResponse<List<ConsultationResponse>> apiResponse = ApiResponse.<List<ConsultationResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your consultations retrieved successfully")
                .data(consultations)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update consultation notes
     * Only the doctor who created the consultation can update it
     */
    @PutMapping("/{consultationId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<ConsultationResponse>> updateConsultation(
            @PathVariable Long consultationId,
            @Valid @RequestBody ConsultationUpdateRequest request) {

        ConsultationResponse response = consultationService.updateConsultation(consultationId, request);

        ApiResponse<ConsultationResponse> apiResponse = ApiResponse.<ConsultationResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation updated successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete consultation
     * Admin only
     */
    @DeleteMapping("/{consultationId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteConsultation(@PathVariable Long consultationId) {

        consultationService.deleteConsultation(consultationId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation deleted successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Search consultations by keyword
     * Searches in all text fields (subjective notes, findings, assessment, plan)
     * Doctors can search their own, admins can search all
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> searchConsultations(
            @RequestParam String keyword) {

        List<ConsultationResponse> consultations = consultationService.searchConsultations(keyword);

        ApiResponse<List<ConsultationResponse>> apiResponse = ApiResponse.<List<ConsultationResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Search completed successfully")
                .data(consultations)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get recent consultations
     * Returns consultations from the last N days
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getRecentConsultations(
            @RequestParam(defaultValue = "30") int days) {

        List<ConsultationResponse> consultations = consultationService.getRecentConsultations(days);

        ApiResponse<List<ConsultationResponse>> apiResponse = ApiResponse.<List<ConsultationResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Recent consultations retrieved successfully")
                .data(consultations)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get consultations within a date range
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ConsultationResponse>>> getConsultationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {

        List<ConsultationResponse> consultations = consultationService.getConsultationsByDateRange(startDate, endDate);

        ApiResponse<List<ConsultationResponse>> apiResponse = ApiResponse.<List<ConsultationResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultations retrieved successfully")
                .data(consultations)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Check if consultation exists for appointment
     * Utility endpoint for checking before creating consultation
     */
    @GetMapping("/exists/appointment/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<Boolean>> consultationExistsForAppointment(
            @PathVariable Long appointmentId) {

        boolean exists = consultationService.consultationExistsForAppointment(appointmentId);

        ApiResponse<Boolean> apiResponse = ApiResponse.<Boolean>builder()
                .statusCode(HttpStatus.OK.value())
                .message(exists ? "Consultation exists" : "Consultation does not exist")
                .data(exists)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get consultation count for a patient
     * Statistics endpoint
     */
    @GetMapping("/stats/patient/{patientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<Long>> getConsultationCountForPatient(
            @PathVariable Long patientId) {

        long count = consultationService.getConsultationCountForPatient(patientId);

        ApiResponse<Long> apiResponse = ApiResponse.<Long>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation count retrieved successfully")
                .data(count)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get consultation count for a doctor
     * Statistics endpoint
     */
    @GetMapping("/stats/doctor/{doctorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getConsultationCountForDoctor(
            @PathVariable Long doctorId) {

        long count = consultationService.getConsultationCountForDoctor(doctorId);

        ApiResponse<Long> apiResponse = ApiResponse.<Long>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation count retrieved successfully")
                .data(count)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}