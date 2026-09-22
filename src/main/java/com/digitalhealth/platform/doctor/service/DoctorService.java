package com.digitalhealth.platform.doctor.service;

import com.digitalhealth.platform.common.enums.Specialization;
import com.digitalhealth.platform.common.exception.BadRequestException;
import com.digitalhealth.platform.common.exception.ConflictException;
import com.digitalhealth.platform.common.exception.ResourceNotFoundException;
import com.digitalhealth.platform.common.exception.UnauthorizedException;
import com.digitalhealth.platform.doctor.dto.*;
import com.digitalhealth.platform.doctor.entity.Doctor;
import com.digitalhealth.platform.doctor.mapper.DoctorMapper;
import com.digitalhealth.platform.doctor.repository.DoctorRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorMapper doctorMapper;

    /**
     * Create a new doctor profile linked to a user
     * Used during doctor onboarding by admin or during self-registration
     */
    @Transactional
    public DoctorResponse createDoctor(DoctorCreateRequest request) {
        log.info("Creating doctor profile for userId: {}", request.getUserId());

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // Check if doctor profile already exists for this user
        if (doctorRepository.findByUser(user).isPresent()) {
            throw new ConflictException("Doctor profile already exists for this user");
        }

        // Validate user has ROLE_DOCTOR
        boolean hasDoctorRole = user.getRoles().stream()
                .map(Role::getName)
                .anyMatch(role -> role.equals("ROLE_DOCTOR"));

        if (!hasDoctorRole) {
            throw new BadRequestException("User must have ROLE_DOCTOR to create a doctor profile");
        }

        // Validate license number uniqueness
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new ConflictException("License number already exists: " + request.getLicenseNumber());
        }

        // Validate license number format (basic validation)
        validateLicenseNumber(request.getLicenseNumber());

        Doctor doctor = Doctor.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .licenseNumber(request.getLicenseNumber().trim().toUpperCase())
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);
        log.info("Doctor profile created successfully with id: {}", savedDoctor.getId());

        return doctorMapper.toResponse(savedDoctor);
    }

    /**
     * Get current authenticated doctor's profile
     */
    public DoctorResponse getCurrentDoctorProfile() {
        log.debug("Fetching current doctor profile");

        User currentUser = getCurrentAuthenticatedUser();
        Doctor doctor = doctorRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for current user"));

        return doctorMapper.toResponse(doctor);
    }

    /**
     * Update current doctor's profile
     * Doctors can update their own information (except license number)
     */
    @Transactional
    public DoctorResponse updateCurrentDoctorProfile(DoctorUpdateRequest request) {
        log.info("Updating current doctor profile");

        User currentUser = getCurrentAuthenticatedUser();
        Doctor doctor = doctorRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for current user"));

        updateDoctorFields(doctor, request);

        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor profile updated successfully for id: {}", updatedDoctor.getId());

        return doctorMapper.toResponse(updatedDoctor);
    }

    /**
     * Get doctor by ID
     * Accessible by: ADMIN, DOCTOR (any doctor can view other doctors), PATIENT (for booking)
     */
    public DoctorResponse getDoctorById(Long doctorId) {
        log.debug("Fetching doctor with id: {}", doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        return doctorMapper.toResponse(doctor);
    }

    /**
     * Get doctor by user ID
     * Useful for linking user accounts to doctor profiles
     */
    public DoctorResponse getDoctorByUserId(Long userId) {
        log.debug("Fetching doctor for userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found for userId: " + userId));

        return doctorMapper.toResponse(doctor);
    }

    /**
     * Get all doctors (summary view)
     * For appointment booking and public doctor search
     */
    public List<DoctorSummaryResponse> getAllDoctorsSummary() {
        log.debug("Fetching all doctors summary");

        return doctorRepository.findAll().stream()
                .map(doctorMapper::toSummary)
                .collect(Collectors.toList());
    }

    /**
     * Get all doctors (full details)
     * For admin use only
     */
    public List<DoctorResponse> getAllDoctors() {
        log.debug("Fetching all doctors");

        return doctorRepository.findAll().stream()
                .map(doctorMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update doctor by ID
     * Used by admins to update doctor information
     */
    @Transactional
    public DoctorResponse updateDoctorById(Long doctorId, DoctorUpdateRequest request) {
        log.info("Updating doctor with id: {}", doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        updateDoctorFields(doctor, request);

        Doctor updatedDoctor = doctorRepository.save(doctor);
        log.info("Doctor updated successfully with id: {}", updatedDoctor.getId());

        return doctorMapper.toResponse(updatedDoctor);
    }

    /**
     * Delete doctor profile
     * Admin only - validates no active appointments exist
     */
    @Transactional
    public void deleteDoctor(Long doctorId) {
        log.info("Deleting doctor with id: {}", doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        // Check for active appointments
        if (doctor.getAppointments() != null && !doctor.getAppointments().isEmpty()) {
            throw new BadRequestException("Cannot delete doctor with existing appointments");
        }

        doctorRepository.delete(doctor);
        log.info("Doctor deleted successfully with id: {}", doctorId);
    }

    /**
     * Search doctors by specialization
     * Public endpoint for patients to find doctors
     */
    public List<DoctorSummaryResponse> searchDoctorsBySpecialization(Specialization specialization) {
        log.debug("Searching doctors by specialization: {}", specialization);

        if (specialization == null) {
            throw new BadRequestException("Specialization is required");
        }

        List<Doctor> doctors = doctorRepository.findBySpecialization(specialization);

        if (doctors.isEmpty()) {
            log.info("No doctors found for specialization: {}", specialization);
        }

        return doctors.stream()
                .map(doctorMapper::toSummary)
                .collect(Collectors.toList());
    }

    /**
     * Search doctors by name
     * For patient's search functionality
     */
    public List<DoctorSummaryResponse> searchDoctorsByName(String searchTerm) {
        log.debug("Searching doctors with term: {}", searchTerm);

        if (!StringUtils.hasText(searchTerm) || searchTerm.length() < 2) {
            throw new BadRequestException("Search term must be at least 2 characters");
        }

        String normalizedSearch = searchTerm.trim().toLowerCase();

        return doctorRepository.findAll().stream()
                .filter(doctor -> {
                    String fullName = (doctor.getFirstName() + " " + doctor.getLastName()).toLowerCase();
                    return fullName.contains(normalizedSearch);
                })
                .map(doctorMapper::toSummary)
                .collect(Collectors.toList());
    }

    /**
     * Get all specialization enums
     */
    public List<Specialization> getAllSpecializations() {
        log.debug("Fetching all specializations");
        return Arrays.asList(Specialization.values());
    }

    /**
     * Verify doctor license number
     * Business logic to check if license is valid and active
     */
    public boolean verifyDoctorLicense(Long doctorId) {
        log.info("Verifying license for doctor id: {}", doctorId);

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        // In a real system, this would call an external API to verify license
        // For now, we just check if license number exists and is formatted correctly
        return StringUtils.hasText(doctor.getLicenseNumber())
                && doctor.getLicenseNumber().length() >= 5;
    }

    /**
     * Get doctors count by specialization
     * For dashboard statistics
     */
    public long getDoctorCountBySpecialization(Specialization specialization) {
        log.debug("Counting doctors for specialization: {}", specialization);
        return doctorRepository.findBySpecialization(specialization).size();
    }

    /**
     * Check if doctor has active appointments
     * Used before deletion or deactivation
     */
    public boolean hasActiveAppointments(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + doctorId));

        return doctor.getAppointments() != null && !doctor.getAppointments().isEmpty();
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Update doctor fields from request
     * Only updates non-null values
     * License number is NOT updatable (business rule for compliance)
     */
    private void updateDoctorFields(Doctor doctor, DoctorUpdateRequest request) {
        if (StringUtils.hasText(request.getFirstName())) {
            doctor.setFirstName(request.getFirstName().trim());
        }

        if (StringUtils.hasText(request.getLastName())) {
            doctor.setLastName(request.getLastName().trim());
        }

        if (request.getSpecialization() != null) {
            doctor.setSpecialization(request.getSpecialization());
        }
    }

    /**
     * Validate license number format
     * Basic validation - in real system would be more complex
     */
    private void validateLicenseNumber(String licenseNumber) {
        if (!StringUtils.hasText(licenseNumber)) {
            throw new BadRequestException("License number is required");
        }

        String trimmed = licenseNumber.trim();

        if (trimmed.length() < 5 || trimmed.length() > 50) {
            throw new BadRequestException("License number must be between 5 and 50 characters");
        }

        // Check for valid characters (alphanumeric and basic punctuation)
        if (!trimmed.matches("^[A-Za-z0-9\\-_/]+$")) {
            throw new BadRequestException("License number contains invalid characters");
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