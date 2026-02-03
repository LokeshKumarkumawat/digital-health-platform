package com.digitalhealth.platform.consultation.service;

import com.digitalhealth.platform.appointment.entity.Appointment;
import com.digitalhealth.platform.appointment.repository.AppointmentRepository;
import com.digitalhealth.platform.common.enums.AppointmentStatus;
import com.digitalhealth.platform.common.exception.BadRequestException;
import com.digitalhealth.platform.common.exception.ResourceNotFoundException;
import com.digitalhealth.platform.common.exception.UnauthorizedException;
import com.digitalhealth.platform.consultation.dto.*;
import com.digitalhealth.platform.consultation.entity.Consultation;
import com.digitalhealth.platform.consultation.mapper.ConsultationMapper;
import com.digitalhealth.platform.consultation.repository.ConsultationRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final ConsultationMapper consultationMapper;

    /**
     * Create consultation notes for a completed appointment
     * Only the assigned doctor can create notes
     */
    @Transactional
    public ConsultationResponse createConsultation(ConsultationCreateRequest request) {
        log.info("Creating consultation for appointmentId: {}", request.getAppointmentId());

        User currentUser = getCurrentAuthenticatedUser();

        // Validate appointment exists
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + request.getAppointmentId()));

        // Security: Only assigned doctor can create consultation notes
        if (!appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the assigned doctor can create consultation notes");
        }

        // Validation: Appointment must be completed
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new BadRequestException("Consultation notes can only be created for completed appointments");
        }

        // Validation: No duplicate consultation notes
        if (consultationRepository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new BadRequestException("Consultation notes already exist for this appointment");
        }

        // Set consultation date to now if not provided
        OffsetDateTime consultationDate = request.getConsultationDate() != null
                ? request.getConsultationDate()
                : OffsetDateTime.now();

        Consultation consultation = Consultation.builder()
                .appointment(appointment)
                .consultationDate(consultationDate)
                .subjectiveNotes(request.getSubjectiveNotes())
                .objectiveFindings(request.getObjectiveFindings())
                .assessment(request.getAssessment())
                .plan(request.getPlan())
                .build();

        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("Consultation created successfully with id: {}", savedConsultation.getId());

        return consultationMapper.toResponse(savedConsultation);
    }

    /**
     * Get consultation by ID
     * Accessible by patient, doctor, or admin
     */
    public ConsultationResponse getConsultationById(Long consultationId) {
        log.debug("Fetching consultation with id: {}", consultationId);

        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consultation not found with id: " + consultationId));

        // Validate user has access
        validateUserAccessToConsultation(consultation);

        return consultationMapper.toResponse(consultation);
    }

    /**
     * Get consultation by appointment ID
     * Accessible by patient, doctor, or admin
     */
    public ConsultationResponse getConsultationByAppointmentId(Long appointmentId) {
        log.debug("Fetching consultation for appointmentId: {}", appointmentId);

        Consultation consultation = consultationRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consultation notes not found for appointment id: " + appointmentId));

        // Validate user has access
        validateUserAccessToConsultation(consultation);

        return consultationMapper.toResponse(consultation);
    }

    /**
     * Get all consultations (admin only)
     */
    public List<ConsultationResponse> getAllConsultations() {
        log.debug("Fetching all consultations");

        return consultationRepository.findAll().stream()
                .map(consultationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get consultation history for a patient
     * If patientId is null, returns history for current authenticated patient
     */
    public List<ConsultationResponse> getConsultationHistoryForPatient(Long patientId) {
        log.debug("Fetching consultation history for patientId: {}", patientId);

        User currentUser = getCurrentAuthenticatedUser();
        Long targetPatientId = patientId;

        // If no patientId provided, get current user's patient profile
        if (targetPatientId == null) {
            Patient currentPatient = patientRepository.findByUser(currentUser)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Patient profile not found for current user"));
            targetPatientId = currentPatient.getId();
        } else {
            // If patientId provided, validate it exists
            if (!patientRepository.existsById(targetPatientId)) {
                throw new ResourceNotFoundException("Patient not found with id: " + targetPatientId);
            }

            // Security: Only patient themselves, their doctors, or admins can view history
            validatePatientHistoryAccess(targetPatientId, currentUser);
        }

        List<Consultation> consultations = consultationRepository
                .findByAppointmentPatientIdOrderByConsultationDateDesc(targetPatientId);

        return consultations.stream()
                .map(consultationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get consultation history for current authenticated patient
     */
    public List<ConsultationResponse> getMyConsultationHistory() {
        log.debug("Fetching consultation history for current patient");

        User currentUser = getCurrentAuthenticatedUser();

        Patient patient = patientRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient profile not found for current user"));

        List<Consultation> consultations = consultationRepository
                .findByAppointmentPatientIdOrderByConsultationDateDesc(patient.getId());

        return consultations.stream()
                .map(consultationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get consultations by doctor ID
     * Returns all consultations created by a specific doctor
     */
    public List<ConsultationResponse> getConsultationsByDoctorId(Long doctorId) {
        log.debug("Fetching consultations for doctorId: {}", doctorId);

        // Validate doctor exists
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }

        List<Consultation> consultations = consultationRepository
                .findByAppointmentDoctorIdOrderByConsultationDateDesc(doctorId);

        return consultations.stream()
                .map(consultationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get consultations for current authenticated doctor
     */
    public List<ConsultationResponse> getMyConsultations() {
        log.debug("Fetching consultations for current doctor");

        User currentUser = getCurrentAuthenticatedUser();

        Doctor doctor = doctorRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor profile not found for current user"));

        List<Consultation> consultations = consultationRepository
                .findByAppointmentDoctorIdOrderByConsultationDateDesc(doctor.getId());

        return consultations.stream()
                .map(consultationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update consultation notes
     * Only the original doctor can update
     */
    @Transactional
    public ConsultationResponse updateConsultation(Long consultationId, ConsultationUpdateRequest request) {
        log.info("Updating consultation with id: {}", consultationId);

        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consultation not found with id: " + consultationId));

        User currentUser = getCurrentAuthenticatedUser();

        // Security: Only the doctor who created the consultation can update it
        if (!consultation.getAppointment().getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Only the doctor who created the consultation can update it");
        }

        // Update fields (partial updates supported)
        if (StringUtils.hasText(request.getSubjectiveNotes())) {
            consultation.setSubjectiveNotes(request.getSubjectiveNotes());
        }

        if (StringUtils.hasText(request.getObjectiveFindings())) {
            consultation.setObjectiveFindings(request.getObjectiveFindings());
        }

        if (StringUtils.hasText(request.getAssessment())) {
            consultation.setAssessment(request.getAssessment());
        }

        if (StringUtils.hasText(request.getPlan())) {
            consultation.setPlan(request.getPlan());
        }

        Consultation updatedConsultation = consultationRepository.save(consultation);
        log.info("Consultation updated successfully with id: {}", updatedConsultation.getId());

        return consultationMapper.toResponse(updatedConsultation);
    }

    /**
     * Delete consultation
     * Admin only
     */
    @Transactional
    public void deleteConsultation(Long consultationId) {
        log.info("Deleting consultation with id: {}", consultationId);

        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Consultation not found with id: " + consultationId));

        consultationRepository.delete(consultation);
        log.info("Consultation deleted successfully with id: {}", consultationId);
    }

    /**
     * Get consultations within a date range
     */
    public List<ConsultationResponse> getConsultationsByDateRange(
            OffsetDateTime startDate, OffsetDateTime endDate) {
        log.debug("Fetching consultations between {} and {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new BadRequestException("Start date and end date are required");
        }

        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date must be after start date");
        }

        List<Consultation> consultations = consultationRepository
                .findByConsultationDateBetween(startDate, endDate);

        return consultations.stream()
                .map(consultationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search consultations by keyword in notes
     * Admin or doctor only
     */
    public List<ConsultationResponse> searchConsultations(String keyword) {
        log.debug("Searching consultations with keyword: {}", keyword);

        if (!StringUtils.hasText(keyword) || keyword.length() < 3) {
            throw new BadRequestException("Search keyword must be at least 3 characters");
        }

        User currentUser = getCurrentAuthenticatedUser();

        // Only allow doctors to search their own consultations, admins can search all
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        List<Consultation> consultations;

        if (isAdmin) {
            consultations = consultationRepository.searchByKeyword(keyword);
        } else {
            Doctor doctor = doctorRepository.findByUser(currentUser)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Doctor profile not found for current user"));

            consultations = consultationRepository.searchByDoctorAndKeyword(doctor.getId(), keyword);
        }

        return consultations.stream()
                .map(consultationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get recent consultations (last 30 days)
     * Doctor or admin only
     */
    public List<ConsultationResponse> getRecentConsultations(int days) {
        log.debug("Fetching consultations from last {} days", days);

        if (days < 1 || days > 365) {
            throw new BadRequestException("Days must be between 1 and 365");
        }

        OffsetDateTime cutoffDate = OffsetDateTime.now().minusDays(days);

        List<Consultation> consultations = consultationRepository
                .findByConsultationDateAfterOrderByConsultationDateDesc(cutoffDate);

        return consultations.stream()
                .map(consultationMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Check if consultation exists for appointment
     */
    public boolean consultationExistsForAppointment(Long appointmentId) {
        return consultationRepository.findByAppointmentId(appointmentId).isPresent();
    }

    /**
     * Get consultation count by patient
     */
    public long getConsultationCountForPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }

        return consultationRepository.countByAppointmentPatientId(patientId);
    }

    /**
     * Get consultation count by doctor
     */
    public long getConsultationCountForDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
        }

        return consultationRepository.countByAppointmentDoctorId(doctorId);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Validate user has access to consultation
     */
    private void validateUserAccessToConsultation(Consultation consultation) {
        User currentUser = getCurrentAuthenticatedUser();

        boolean isPatient = consultation.getAppointment().getPatient().getUser().getId()
                .equals(currentUser.getId());
        boolean isDoctor = consultation.getAppointment().getDoctor().getUser().getId()
                .equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isPatient && !isDoctor && !isAdmin) {
            throw new UnauthorizedException("You do not have permission to access this consultation");
        }
    }

    /**
     * Validate user can access patient consultation history
     */
    private void validatePatientHistoryAccess(Long patientId, User currentUser) {
        // Check if user is the patient
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        boolean isPatient = patient.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        // Check if user is a doctor who has treated this patient
        boolean isDoctor = false;
        if (!isPatient && !isAdmin) {
            Doctor doctor = doctorRepository.findByUser(currentUser).orElse(null);
            if (doctor != null) {
                isDoctor = consultationRepository
                        .existsByAppointmentPatientIdAndAppointmentDoctorId(patientId, doctor.getId());
            }
        }

        if (!isPatient && !isDoctor && !isAdmin) {
            throw new UnauthorizedException("You do not have permission to view this patient's consultation history");
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