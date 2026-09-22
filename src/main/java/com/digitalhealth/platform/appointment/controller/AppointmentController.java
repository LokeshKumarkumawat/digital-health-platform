package com.digitalhealth.platform.appointment.controller;

import com.digitalhealth.platform.appointment.dto.*;
import com.digitalhealth.platform.appointment.service.AppointmentService;
import com.digitalhealth.platform.common.enums.AppointmentStatus;
import com.digitalhealth.platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Appointment Management
 *
 * Endpoints:
 * - POST   /api/v1/appointments                 - Book appointment (PATIENT, ADMIN)
 * - GET    /api/v1/appointments/me              - Get current user's appointments
 * - GET    /api/v1/appointments/{id}            - Get appointment by ID
 * - PUT    /api/v1/appointments/{id}            - Update appointment (reschedule)
 * - PUT    /api/v1/appointments/{id}/status     - Update appointment status
 * - DELETE /api/v1/appointments/{id}            - Delete appointment (ADMIN)
 * - PUT    /api/v1/appointments/{id}/cancel     - Cancel appointment
 * - PUT    /api/v1/appointments/{id}/complete   - Complete appointment (DOCTOR)
 * - PUT    /api/v1/appointments/{id}/no-show    - Mark as no-show (DOCTOR, ADMIN)
 * - GET    /api/v1/appointments                 - Get all appointments (ADMIN)
 * - GET    /api/v1/appointments/doctor/{id}     - Get appointments by doctor
 * - GET    /api/v1/appointments/patient/{id}    - Get appointments by patient
 * - GET    /api/v1/appointments/status/{status} - Get appointments by status
 */
@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Book a new appointment
     * Patients can book for themselves, admins can book for any patient
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_PATIENT') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            @Valid @RequestBody AppointmentCreateRequest request) {

        AppointmentResponse response = appointmentService.bookAppointment(request);

        ApiResponse<AppointmentResponse> apiResponse = ApiResponse.<AppointmentResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Appointment booked successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Get current authenticated user's appointments
     * Returns doctor's appointments if user is doctor, patient's appointments otherwise
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMyAppointments() {

        List<AppointmentResponse> appointments = appointmentService.getMyAppointments();

        ApiResponse<List<AppointmentResponse>> apiResponse = ApiResponse.<List<AppointmentResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointments retrieved successfully")
                .data(appointments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get appointment by ID
     * Accessible by patient, doctor, or admin involved in the appointment
     */
    @GetMapping("/{appointmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentById(
            @PathVariable Long appointmentId) {

        AppointmentResponse response = appointmentService.getAppointmentById(appointmentId);

        ApiResponse<AppointmentResponse> apiResponse = ApiResponse.<AppointmentResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all appointments
     * Admin only - returns all appointments in the system
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAllAppointments() {

        List<AppointmentResponse> appointments = appointmentService.getAllAppointments();

        ApiResponse<List<AppointmentResponse>> apiResponse = ApiResponse.<List<AppointmentResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All appointments retrieved successfully")
                .data(appointments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get appointments by doctor ID
     * Accessible by the doctor themselves, admins, or for public doctor schedule viewing
     */
    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByDoctorId(
            @PathVariable Long doctorId) {

        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);

        ApiResponse<List<AppointmentResponse>> apiResponse = ApiResponse.<List<AppointmentResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor appointments retrieved successfully")
                .data(appointments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get appointments by patient ID
     * Accessible by the patient themselves or admins
     */
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @appointmentService.getAppointmentsByPatientId(#patientId).get(0).patientId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByPatientId(
            @PathVariable Long patientId) {

        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByPatientId(patientId);

        ApiResponse<List<AppointmentResponse>> apiResponse = ApiResponse.<List<AppointmentResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient appointments retrieved successfully")
                .data(appointments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get appointments by status
     * Admin and doctors can filter appointments by status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAppointmentsByStatus(
            @PathVariable AppointmentStatus status) {

        List<AppointmentResponse> appointments = appointmentService.getAppointmentsByStatus(status);

        ApiResponse<List<AppointmentResponse>> apiResponse = ApiResponse.<List<AppointmentResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointments with status " + status + " retrieved successfully")
                .data(appointments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update appointment (reschedule or update details)
     * Accessible by patient, doctor, or admin
     */
    @PutMapping("/{appointmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentUpdateRequest request) {

        AppointmentResponse response = appointmentService.updateAppointment(appointmentId, request);

        ApiResponse<AppointmentResponse> apiResponse = ApiResponse.<AppointmentResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment updated successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Update appointment status
     * Different roles have different permissions for status changes
     */
    @PutMapping("/{appointmentId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentStatusUpdateRequest request) {

        AppointmentResponse response = appointmentService.updateAppointmentStatus(appointmentId, request);

        ApiResponse<AppointmentResponse> apiResponse = ApiResponse.<AppointmentResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment status updated successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Cancel appointment
     * Can be done by patient, doctor, or admin
     */
    @PutMapping("/{appointmentId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> cancelAppointment(@PathVariable Long appointmentId) {

        appointmentService.cancelAppointment(appointmentId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment cancelled successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Complete appointment
     * Only the assigned doctor can mark appointment as completed
     */
    @PutMapping("/{appointmentId}/complete")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<ApiResponse<AppointmentResponse>> completeAppointment(
            @PathVariable Long appointmentId) {

        AppointmentResponse response = appointmentService.completeAppointment(appointmentId);

        ApiResponse<AppointmentResponse> apiResponse = ApiResponse.<AppointmentResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment marked as completed successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Mark appointment as no-show
     * Only doctor or admin can mark as no-show
     */
    @PutMapping("/{appointmentId}/no-show")
    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> markAsNoShow(@PathVariable Long appointmentId) {

        appointmentService.markAsNoShow(appointmentId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment marked as no-show successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Delete appointment
     * Admin only - hard delete
     */
    @DeleteMapping("/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable Long appointmentId) {

        appointmentService.deleteAppointment(appointmentId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment deleted successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}