package com.digitalhealth.platform.doctor.repository;

import com.digitalhealth.platform.common.enums.Specialization;
import com.digitalhealth.platform.doctor.entity.Doctor;
import com.digitalhealth.platform.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Doctor entity
 * Provides database operations for doctor management
 */
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    /**
     * Find doctor by associated user
     * @param user The user entity
     * @return Optional containing the doctor if found
     */
    Optional<Doctor> findByUser(User user);

    /**
     * Find all doctors by specialization
     * @param specialization The specialization to search for
     * @return List of doctors with the specified specialization
     */
    List<Doctor> findBySpecialization(Specialization specialization);

    /**
     * Check if a doctor with the given license number exists
     * @param licenseNumber The license number to check
     * @return true if exists, false otherwise
     */
    boolean existsByLicenseNumber(String licenseNumber);

    /**
     * Find doctor by license number
     * @param licenseNumber The license number
     * @return Optional containing the doctor if found
     */
    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    /**
     * Find doctors by first name or last name containing search term (case-insensitive)
     * @param firstName Search term for first name
     * @param lastName Search term for last name
     * @return List of matching doctors
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) " +
            "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<Doctor> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName
    );

    /**
     * Find doctors by full name search (first name + last name)
     * @param searchTerm The search term
     * @return List of matching doctors
     */
    @Query("SELECT d FROM Doctor d WHERE LOWER(CONCAT(d.firstName, ' ', d.lastName)) " +
            "LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Doctor> searchByFullName(@Param("searchTerm") String searchTerm);

    /**
     * Count doctors by specialization
     * @param specialization The specialization
     * @return Number of doctors with that specialization
     */
    long countBySpecialization(Specialization specialization);

    /**
     * Find all doctors ordered by last name
     * @return List of all doctors sorted by last name
     */
    @Query("SELECT d FROM Doctor d ORDER BY d.lastName ASC, d.firstName ASC")
    List<Doctor> findAllOrderedByName();

    /**
     * Find doctors with appointments count greater than specified value
     * Useful for finding active doctors
     * @param appointmentCount Minimum number of appointments
     * @return List of doctors with appointments count >= appointmentCount
     */
    @Query("SELECT d FROM Doctor d WHERE SIZE(d.appointments) >= :appointmentCount")
    List<Doctor> findDoctorsWithAppointmentsGreaterThan(@Param("appointmentCount") int appointmentCount);

    /**
     * Find doctors without any appointments
     * @return List of doctors with no appointments
     */
    @Query("SELECT d FROM Doctor d WHERE SIZE(d.appointments) = 0")
    List<Doctor> findDoctorsWithoutAppointments();

    /**
     * Check if doctor exists by user ID
     * @param userId The user ID
     * @return true if doctor profile exists for this user
     */
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Doctor d WHERE d.user.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}