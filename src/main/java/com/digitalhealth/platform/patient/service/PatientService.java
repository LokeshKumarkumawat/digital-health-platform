package com.digitalhealth.platform.patient.service;

import com.digitalhealth.platform.common.enums.BloodGroup;
import com.digitalhealth.platform.common.enums.Genotype;
import com.digitalhealth.platform.common.exception.BadRequestException;
import com.digitalhealth.platform.common.exception.ConflictException;
import com.digitalhealth.platform.common.exception.ResourceNotFoundException;
import com.digitalhealth.platform.common.exception.UnauthorizedException;
import com.digitalhealth.platform.patient.dto.PatientCreateRequest;
import com.digitalhealth.platform.patient.dto.PatientResponse;
import com.digitalhealth.platform.patient.dto.PatientSummaryResponse;
import com.digitalhealth.platform.patient.dto.PatientUpdateRequest;
import com.digitalhealth.platform.patient.entity.Patient;
import com.digitalhealth.platform.patient.mapper.PatientMapper;
import com.digitalhealth.platform.patient.repository.PatientRepository;
import com.digitalhealth.platform.role.entity.Role;
import com.digitalhealth.platform.users.entity.User;
import com.digitalhealth.platform.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientMapper patientMapper;

    /**
     * Create a new patient profile linked to a user
     * Used during user onboarding or admin registration
     */
    @Transactional
    public PatientResponse createPatient(PatientCreateRequest request) {
        log.info("Creating patient profile for userId: {}", request.getUserId());

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // Check if patient profile already exists for this user
        if (patientRepository.findByUser(user).isPresent()) {
            throw new ConflictException("Patient profile already exists for this user");
        }

        // Validate user has ROLE_PATIENT
        boolean hasPatientRole = user.getRoles().stream()
                .map(Role::getName)
                .anyMatch(role -> role.equals("ROLE_PATIENT"));

        if (!hasPatientRole) {
            throw new BadRequestException("User must have ROLE_PATIENT to create a patient profile");
        }

        // Validate age (must be realistic)
        validateDateOfBirth(request.getDateOfBirth());

        Patient patient = Patient.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .knownAllergies(request.getKnownAllergies())
                .bloodGroup(request.getBloodGroup())
                .genotype(request.getGenotype())
                .build();

        Patient savedPatient = patientRepository.save(patient);
        log.info("Patient profile created successfully with id: {}", savedPatient.getId());

        return patientMapper.toResponse(savedPatient);
    }

    /**
     * Get current authenticated patient's profile
     */
    public PatientResponse getCurrentPatientProfile() {
        log.debug("Fetching current patient profile");

        User currentUser = getCurrentAuthenticatedUser();
        Patient patient = patientRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for current user"));

        return patientMapper.toResponse(patient);
    }

    /**
     * Update current patient's profile
     * Patients can update their own information
     */
    @Transactional
    public PatientResponse updateCurrentPatientProfile(PatientUpdateRequest request) {
        log.info("Updating current patient profile");

        User currentUser = getCurrentAuthenticatedUser();
        Patient patient = patientRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for current user"));

        updatePatientFields(patient, request);

        Patient updatedPatient = patientRepository.save(patient);
        log.info("Patient profile updated successfully for id: {}", updatedPatient.getId());

        return patientMapper.toResponse(updatedPatient);
    }

    /**
     * Get patient by ID
     * Accessible by: ADMIN, DOCTOR, or the patient themselves
     */
    public PatientResponse getPatientById(Long patientId) {
        log.debug("Fetching patient with id: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        // Authorization check handled by @PreAuthorize in controller
        return patientMapper.toResponse(patient);
    }

    /**
     * Get patient by user ID
     * Useful for linking user accounts to patient profiles
     */
    public PatientResponse getPatientByUserId(Long userId) {
        log.debug("Fetching patient for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Patient patient = patientRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found for userId: " + userId));

        return patientMapper.toResponse(patient);
    }

    /**
     * Get all patients (summary view)
     * For admin dashboard and doctor's patient lists
     */
    public List<PatientSummaryResponse> getAllPatientsSummary() {
        log.debug("Fetching all patients summary");

        return patientRepository.findAll().stream()
                .map(patientMapper::toSummary)
                .collect(Collectors.toList());
    }

    /**
     * Get all patients (full details)
     * For admin use only
     */
    public List<PatientResponse> getAllPatients() {
        log.debug("Fetching all patients");

        return patientRepository.findAll().stream()
                .map(patientMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update patient by ID
     * Used by doctors or admins to update patient medical information
     */
    @Transactional
    public PatientResponse updatePatientById(Long patientId, PatientUpdateRequest request) {
        log.info("Updating patient with id: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        updatePatientFields(patient, request);

        Patient updatedPatient = patientRepository.save(patient);
        log.info("Patient updated successfully with id: {}", updatedPatient.getId());

        return patientMapper.toResponse(updatedPatient);
    }

    /**
     * Delete patient profile
     * Admin only - also validates no active appointments
     */
    @Transactional
    public void deletePatient(Long patientId) {
        log.info("Deleting patient with id: {}", patientId);

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        // Check for active appointments
        if (patient.getAppointments() != null && !patient.getAppointments().isEmpty()) {
            throw new BadRequestException("Cannot delete patient with existing appointments");
        }

        patientRepository.delete(patient);
        log.info("Patient deleted successfully with id: {}", patientId);
    }

    /**
     * Get all blood group enums
     */
    public List<BloodGroup> getAllBloodGroups() {
        log.debug("Fetching all blood groups");
        return Arrays.asList(BloodGroup.values());
    }

    /**
     * Get all genotype enums
     */
    public List<Genotype> getAllGenotypes() {
        log.debug("Fetching all genotypes");
        return Arrays.asList(Genotype.values());
    }

    /**
     * Calculate patient age
     */
    public Integer calculateAge(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        return Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
    }

    /**
     * Search patients by name
     * For doctor's search functionality
     */
    public List<PatientSummaryResponse> searchPatientsByName(String searchTerm) {
        log.debug("Searching patients with term: {}", searchTerm);

        if (!StringUtils.hasText(searchTerm) || searchTerm.length() < 2) {
            throw new BadRequestException("Search term must be at least 2 characters");
        }

        String normalizedSearch = searchTerm.trim().toLowerCase();

        return patientRepository.findAll().stream()
                .filter(patient -> {
                    String fullName = (patient.getFirstName() + " " + patient.getLastName()).toLowerCase();
                    return fullName.contains(normalizedSearch);
                })
                .map(patientMapper::toSummary)
                .collect(Collectors.toList());
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Update patient fields from request
     * Only updates non-null values
     */
    private void updatePatientFields(Patient patient, PatientUpdateRequest request) {
        if (StringUtils.hasText(request.getFirstName())) {
            patient.setFirstName(request.getFirstName().trim());
        }

        if (StringUtils.hasText(request.getLastName())) {
            patient.setLastName(request.getLastName().trim());
        }

        if (request.getDateOfBirth() != null) {
            validateDateOfBirth(request.getDateOfBirth());
            patient.setDateOfBirth(request.getDateOfBirth());
        }

        if (StringUtils.hasText(request.getPhone())) {
            patient.setPhone(request.getPhone().trim());
        }

        if (request.getKnownAllergies() != null) {
            patient.setKnownAllergies(request.getKnownAllergies().trim());
        }

        if (request.getBloodGroup() != null) {
            patient.setBloodGroup(request.getBloodGroup());
        }

        if (request.getGenotype() != null) {
            patient.setGenotype(request.getGenotype());
        }
    }

    /**
     * Validate date of birth
     * Must be in the past and result in reasonable age (0-150 years)
     */
    private void validateDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth == null) {
            throw new BadRequestException("Date of birth is required");
        }

        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new BadRequestException("Date of birth cannot be in the future");
        }

        int age = Period.between(dateOfBirth, LocalDate.now()).getYears();
        if (age > 150) {
            throw new BadRequestException("Invalid date of birth: age cannot exceed 150 years");
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