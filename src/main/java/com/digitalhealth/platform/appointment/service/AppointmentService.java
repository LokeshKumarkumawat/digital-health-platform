package com.digitalhealth.platform.appointment.service;

import com.digitalhealth.platform.appointment.dto.*;
import com.digitalhealth.platform.appointment.entity.Appointment;
import com.digitalhealth.platform.appointment.mapper.AppointmentMapper;
import com.digitalhealth.platform.appointment.repository.AppointmentRepository;
import com.digitalhealth.platform.common.enums.AppointmentStatus;
import com.digitalhealth.platform.common.exception.BadRequestException;
import com.digitalhealth.platform.common.exception.ResourceNotFoundException;
import com.digitalhealth.platform.common.exception.UnauthorizedException;
import com.digitalhealth.platform.doctor.entity.Doctor;
import com.digitalhealth.platform.doctor.repository.DoctorRepository;
import com.digitalhealth.platform.patient.entity.Patient;
import com.digitalhealth.platform.patient.repository.PatientRepository;
import com.digitalhealth.platform.users.entity.User;
import com.digitalhealth.platform.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper appointmentMapper;

    // Business rule: appointments must be booked at least 1 hour in advance
    private static final int MIN_BOOKING_HOURS = 1;
    // Business rule: standard appointment duration
    private static final int APPOINTMENT_DURATION_MINUTES = 60;
    // Business rule: mandatory break between appointments
    private static final int BUFFER_MINUTES = 60;

    /**
     * Book a new appointment
     * Validates time availability and generates meeting link
     */
    @Transactional
    public AppointmentResponse bookAppointment(AppointmentCreateRequest request) {
        log.info("Booking appointment for doctorId: {} and patientId: {}",
                request.getDoctorId(), request.getPatientId());

        // Validate patient exists
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));

        // Validate doctor exists
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        // Validate and process appointment times
        OffsetDateTime startTime = request.getStartTime();
        OffsetDateTime endTime = request.getEndTime() != null
                ? request.getEndTime()
                : startTime.plusMinutes(APPOINTMENT_DURATION_MINUTES);

        // Validation: appointment must be at least 1 hour in advance
        if (startTime.isBefore(OffsetDateTime.now().plusHours(MIN_BOOKING_HOURS))) {
            throw new BadRequestException(
                    String.format("Appointments must be booked at least %d hour(s) in advance", MIN_BOOKING_HOURS));
        }

        // Validation: end time must be after start time
        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            throw new BadRequestException("End time must be after start time");
        }

        // Validation: reasonable appointment duration (15 min to 4 hours)
        long durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if (durationMinutes < 15 || durationMinutes > 240) {
            throw new BadRequestException("Appointment duration must be between 15 minutes and 4 hours");
        }

        // Check for scheduling conflicts with buffer
        checkAppointmentConflicts(doctor.getId(), startTime, endTime);

        // Generate meeting link
        String meetingLink = generateMeetingLink();

        // Create appointment
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .startTime(startTime)
                .endTime(endTime)
                .purposeOfConsultation(request.getPurposeOfConsultation())
                .initialSymptoms(request.getInitialSymptoms())
                .meetingLink(meetingLink)
                .status(AppointmentStatus.SCHEDULED)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment booked successfully with id: {}", savedAppointment.getId());

        // TODO: Send email notifications to patient and doctor
        // sendAppointmentConfirmation(savedAppointment);

        return appointmentMapper.toResponse(savedAppointment);
    }

    /**
     * Get all appointments for current authenticated user
     * Returns doctor's appointments if user is a doctor, patient's appointments otherwise
     */
    public List<AppointmentResponse> getMyAppointments() {
        log.debug("Fetching appointments for current user");

        User currentUser = getCurrentAuthenticatedUser();
        List<Appointment> appointments;

        // Check if user is a doctor
        boolean isDoctor = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_DOCTOR"));

        if (isDoctor) {
            Doctor doctor = doctorRepository.findByUser(currentUser)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for current user"));

            appointments = appointmentRepository.findByDoctorIdOrderByStartTimeDesc(doctor.getId());
        } else {
            Patient patient = patientRepository.findByUser(currentUser)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for current user"));

            appointments = appointmentRepository.findByPatientIdOrderByStartTimeDesc(patient.getId());
        }

        return appointments.stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get appointment by ID
     * Only accessible by the patient, doctor, or admin involved
     */
    public AppointmentResponse getAppointmentById(Long appointmentId) {
        log.debug("Fetching appointment with id: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        // Authorization check (handled by controller @PreAuthorize or here)
        validateUserAccessToAppointment(appointment);

        return appointmentMapper.toResponse(appointment);
    }

    /**
     * Get all appointments (admin only)
     */
    public List<AppointmentResponse> getAllAppointments() {
        log.debug("Fetching all appointments");

        return appointmentRepository.findAll().stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments by doctor ID
     */
    public List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId) {
        log.debug("Fetching appointments for doctorId: {}", doctorId);

        // Validate doctor exists
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }

        List<Appointment> appointments = appointmentRepository.findByDoctorIdOrderByStartTimeDesc(doctorId);

        return appointments.stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments by patient ID
     */
    public List<AppointmentResponse> getAppointmentsByPatientId(Long patientId) {
        log.debug("Fetching appointments for patientId: {}", patientId);

        // Validate patient exists
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientIdOrderByStartTimeDesc(patientId);

        return appointments.stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments by status
     */
    public List<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status) {
        log.debug("Fetching appointments with status: {}", status);

        List<Appointment> appointments = appointmentRepository.findByStatusOrderByStartTimeDesc(status);

        return appointments.stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update appointment details (reschedule, update purpose, etc.)
     */
    @Transactional
    public AppointmentResponse updateAppointment(Long appointmentId, AppointmentUpdateRequest request) {
        log.info("Updating appointment with id: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        // Only allow updates for scheduled appointments
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BadRequestException("Only scheduled appointments can be updated");
        }

        // Validate user has permission to update
        validateUserAccessToAppointment(appointment);

        // Update times if provided
        if (request.getStartTime() != null) {
            OffsetDateTime newStartTime = request.getStartTime();
            OffsetDateTime newEndTime = request.getEndTime() != null
                    ? request.getEndTime()
                    : newStartTime.plusMinutes(APPOINTMENT_DURATION_MINUTES);

            // Validate new time
            if (newStartTime.isBefore(OffsetDateTime.now().plusHours(MIN_BOOKING_HOURS))) {
                throw new BadRequestException("Appointments must be scheduled at least 1 hour in advance");
            }

            // Check for conflicts (excluding current appointment)
            checkAppointmentConflicts(appointment.getDoctor().getId(), newStartTime, newEndTime, appointmentId);

            appointment.setStartTime(newStartTime);
            appointment.setEndTime(newEndTime);
        }

        // Update other fields
        if (StringUtils.hasText(request.getPurposeOfConsultation())) {
            appointment.setPurposeOfConsultation(request.getPurposeOfConsultation());
        }

        if (request.getInitialSymptoms() != null) {
            appointment.setInitialSymptoms(request.getInitialSymptoms());
        }

        if (StringUtils.hasText(request.getMeetingLink())) {
            appointment.setMeetingLink(request.getMeetingLink());
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment updated successfully with id: {}", updatedAppointment.getId());

        // TODO: Send notification about update
        // sendAppointmentUpdateNotification(updatedAppointment);

        return appointmentMapper.toResponse(updatedAppointment);
    }

    /**
     * Update appointment status
     * Used for completing, cancelling, or marking as no-show
     */
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatusUpdateRequest request) {
        log.info("Updating status for appointment id: {} to {}", appointmentId, request.getStatus());

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        User currentUser = getCurrentAuthenticatedUser();
        AppointmentStatus newStatus = request.getStatus();

        // Validate status transition based on user role
        validateStatusUpdate(appointment, newStatus, currentUser);

        // Update status and set end time if completing
        appointment.setStatus(newStatus);

        if (newStatus == AppointmentStatus.COMPLETED && appointment.getEndTime() == null) {
            appointment.setEndTime(OffsetDateTime.now());
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment status updated successfully to: {}", newStatus);

        // TODO: Send appropriate notifications
        // sendStatusChangeNotification(updatedAppointment, currentUser);

        return appointmentMapper.toResponse(updatedAppointment);
    }

    /**
     * Cancel appointment
     * Can be done by patient, doctor, or admin
     */
    @Transactional
    public void cancelAppointment(Long appointmentId) {
        log.info("Cancelling appointment with id: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        // Validate user has permission to cancel
        validateUserAccessToAppointment(appointment);

        // Only allow cancellation of scheduled appointments
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BadRequestException("Only scheduled appointments can be cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        log.info("Appointment cancelled successfully with id: {}", appointmentId);

        // TODO: Send cancellation notifications
        // sendCancellationNotification(appointment, getCurrentAuthenticatedUser());
    }

    /**
     * Complete appointment (doctor only)
     */
    @Transactional
    public AppointmentResponse completeAppointment(Long appointmentId) {
        log.info("Completing appointment with id: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        User currentUser = getCurrentAuthenticatedUser();

        // Validate current user is the assigned doctor
        if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the assigned doctor can complete this appointment");
        }

        // Validate appointment is scheduled
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BadRequestException("Only scheduled appointments can be marked as completed");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setEndTime(OffsetDateTime.now());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment completed successfully with id: {}", updatedAppointment.getId());

        return appointmentMapper.toResponse(updatedAppointment);
    }

    /**
     * Mark appointment as no-show
     */
    @Transactional
    public void markAsNoShow(Long appointmentId) {
        log.info("Marking appointment as no-show with id: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        User currentUser = getCurrentAuthenticatedUser();

        // Only doctor or admin can mark as no-show
        boolean isDoctor = appointment.getDoctor().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isDoctor && !isAdmin) {
            throw new UnauthorizedException("Only the assigned doctor or admin can mark appointment as no-show");
        }

        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointmentRepository.save(appointment);

        log.info("Appointment marked as no-show with id: {}", appointmentId);
    }

    /**
     * Delete appointment (admin only, soft delete by cancelling)
     */
    @Transactional
    public void deleteAppointment(Long appointmentId) {
        log.info("Deleting appointment with id: {}", appointmentId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + appointmentId));

        // Check if appointment has consultation notes
        if (appointment.getConsultation() != null) {
            throw new BadRequestException("Cannot delete appointment with existing consultation notes");
        }

        appointmentRepository.delete(appointment);
        log.info("Appointment deleted successfully with id: {}", appointmentId);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Check for appointment conflicts with buffer time
     */
    private void checkAppointmentConflicts(Long doctorId, OffsetDateTime startTime, OffsetDateTime endTime) {
        checkAppointmentConflicts(doctorId, startTime, endTime, null);
    }

    /**
     * Check for appointment conflicts excluding a specific appointment (for updates)
     */
    private void checkAppointmentConflicts(Long doctorId, OffsetDateTime startTime,
                                           OffsetDateTime endTime, Long excludeAppointmentId) {

        // Add buffer time before the appointment
        OffsetDateTime checkStart = startTime.minusMinutes(BUFFER_MINUTES);

        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                doctorId, checkStart, endTime);

        // Filter out the excluded appointment if updating
        if (excludeAppointmentId != null) {
            conflicts = conflicts.stream()
                    .filter(a -> !a.getId().equals(excludeAppointmentId))
                    .collect(Collectors.toList());
        }

        if (!conflicts.isEmpty()) {
            throw new BadRequestException(
                    "Doctor is not available at the requested time. Please choose a different time slot.");
        }
    }

    /**
     * Generate unique meeting link using Jitsi
     */
    private String generateMeetingLink() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String uniqueRoomName = "digitalhealth-" + uuid.substring(0, 10);
        String meetingLink = "https://meet.jit.si/" + uniqueRoomName;

        log.debug("Generated meeting link: {}", meetingLink);
        return meetingLink;
    }

    /**
     * Validate user has access to appointment
     */
    private void validateUserAccessToAppointment(Appointment appointment) {
        User currentUser = getCurrentAuthenticatedUser();

        boolean isPatient = appointment.getPatient().getUser().getId().equals(currentUser.getId());
        boolean isDoctor = appointment.getDoctor().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isPatient && !isDoctor && !isAdmin) {
            throw new UnauthorizedException("You do not have permission to access this appointment");
        }
    }

    /**
     * Validate status update based on user role and current status
     */
    private void validateStatusUpdate(Appointment appointment, AppointmentStatus newStatus, User currentUser) {
        boolean isDoctor = appointment.getDoctor().getUser().getId().equals(currentUser.getId());
        boolean isPatient = appointment.getPatient().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        switch (newStatus) {
            case COMPLETED:
                if (!isDoctor && !isAdmin) {
                    throw new UnauthorizedException("Only doctors can mark appointments as completed");
                }
                break;
            case CANCELLED:
                if (!isDoctor && !isPatient && !isAdmin) {
                    throw new UnauthorizedException("You do not have permission to cancel this appointment");
                }
                break;
            case NO_SHOW:
                if (!isDoctor && !isAdmin) {
                    throw new UnauthorizedException("Only doctors or admins can mark appointments as no-show");
                }
                break;
            case SCHEDULED:
                // Re-scheduling is allowed for admins only
                if (!isAdmin) {
                    throw new UnauthorizedException("Only admins can change status back to scheduled");
                }
                break;
        }
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("User not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}