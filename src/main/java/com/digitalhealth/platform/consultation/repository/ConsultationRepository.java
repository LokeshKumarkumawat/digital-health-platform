package com.digitalhealth.platform.consultation.repository;

import com.digitalhealth.platform.consultation.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Consultation entity
 * Provides database operations for consultation management
 */
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    /**
     * Find consultation by appointment ID
     * One-to-one relationship means there's only one consultation per appointment
     * @param appointmentId The appointment ID
     * @return Optional containing the consultation if found
     */
    Optional<Consultation> findByAppointmentId(Long appointmentId);

    /**
     * Find all consultations for a patient ordered by consultation date descending
     * @param patientId The patient ID
     * @return List of consultations for the patient
     */
    List<Consultation> findByAppointmentPatientIdOrderByConsultationDateDesc(Long patientId);

    /**
     * Find all consultations created by a doctor ordered by consultation date descending
     * @param doctorId The doctor ID
     * @return List of consultations created by the doctor
     */
    List<Consultation> findByAppointmentDoctorIdOrderByConsultationDateDesc(Long doctorId);

    /**
     * Find consultations within a date range
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of consultations within the range
     */
    @Query("SELECT c FROM Consultation c WHERE c.consultationDate >= :startDate " +
            "AND c.consultationDate <= :endDate ORDER BY c.consultationDate DESC")
    List<Consultation> findByConsultationDateBetween(
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Find consultations after a specific date
     * @param date The cutoff date
     * @return List of consultations after the date
     */
    List<Consultation> findByConsultationDateAfterOrderByConsultationDateDesc(OffsetDateTime date);

    /**
     * Find consultations before a specific date
     * @param date The cutoff date
     * @return List of consultations before the date
     */
    List<Consultation> findByConsultationDateBeforeOrderByConsultationDateDesc(OffsetDateTime date);

    /**
     * Search consultations by keyword in any text field
     * Searches in subjective notes, objective findings, assessment, and plan
     * @param keyword The search keyword
     * @return List of matching consultations
     */
    @Query("SELECT c FROM Consultation c WHERE " +
            "LOWER(c.subjectiveNotes) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.objectiveFindings) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.assessment) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.plan) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY c.consultationDate DESC")
    List<Consultation> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Search consultations by keyword for a specific doctor
     * @param doctorId The doctor ID
     * @param keyword The search keyword
     * @return List of matching consultations
     */
    @Query("SELECT c FROM Consultation c WHERE c.appointment.doctor.id = :doctorId AND (" +
            "LOWER(c.subjectiveNotes) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.objectiveFindings) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.assessment) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.plan) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY c.consultationDate DESC")
    List<Consultation> searchByDoctorAndKeyword(
            @Param("doctorId") Long doctorId,
            @Param("keyword") String keyword
    );

    /**
     * Search consultations by keyword for a specific patient
     * @param patientId The patient ID
     * @param keyword The search keyword
     * @return List of matching consultations
     */
    @Query("SELECT c FROM Consultation c WHERE c.appointment.patient.id = :patientId AND (" +
            "LOWER(c.subjectiveNotes) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.objectiveFindings) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.assessment) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.plan) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY c.consultationDate DESC")
    List<Consultation> searchByPatientAndKeyword(
            @Param("patientId") Long patientId,
            @Param("keyword") String keyword
    );

    /**
     * Count consultations for a patient
     * @param patientId The patient ID
     * @return Count of consultations
     */
    long countByAppointmentPatientId(Long patientId);

    /**
     * Count consultations for a doctor
     * @param doctorId The doctor ID
     * @return Count of consultations
     */
    long countByAppointmentDoctorId(Long doctorId);

    /**
     * Check if a doctor has treated a specific patient
     * @param patientId The patient ID
     * @param doctorId The doctor ID
     * @return true if doctor has consultation with patient
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Consultation c " +
            "WHERE c.appointment.patient.id = :patientId AND c.appointment.doctor.id = :doctorId")
    boolean existsByAppointmentPatientIdAndAppointmentDoctorId(
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId
    );

    /**
     * Find consultations for a patient within a date range
     * @param patientId The patient ID
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of consultations
     */
    @Query("SELECT c FROM Consultation c WHERE c.appointment.patient.id = :patientId " +
            "AND c.consultationDate >= :startDate AND c.consultationDate <= :endDate " +
            "ORDER BY c.consultationDate DESC")
    List<Consultation> findByPatientAndDateRange(
            @Param("patientId") Long patientId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Find consultations for a doctor within a date range
     * @param doctorId The doctor ID
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of consultations
     */
    @Query("SELECT c FROM Consultation c WHERE c.appointment.doctor.id = :doctorId " +
            "AND c.consultationDate >= :startDate AND c.consultationDate <= :endDate " +
            "ORDER BY c.consultationDate DESC")
    List<Consultation> findByDoctorAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Find most recent consultation for a patient
     * @param patientId The patient ID
     * @return Optional containing the most recent consultation
     */
    @Query("SELECT c FROM Consultation c WHERE c.appointment.patient.id = :patientId " +
            "ORDER BY c.consultationDate DESC LIMIT 1")
    Optional<Consultation> findMostRecentByPatientId(@Param("patientId") Long patientId);

    /**
     * Find consultations with incomplete notes
     * Finds consultations where any of the key fields are null or empty
     * @return List of incomplete consultations
     */
    @Query("SELECT c FROM Consultation c WHERE " +
            "c.assessment IS NULL OR c.assessment = '' OR " +
            "c.plan IS NULL OR c.plan = '' " +
            "ORDER BY c.consultationDate DESC")
    List<Consultation> findIncompleteConsultations();

    /**
     * Delete all consultations for a patient
     * Used when patient account is deleted
     * @param patientId The patient ID
     */
    void deleteByAppointmentPatientId(Long patientId);

    /**
     * Delete all consultations for a doctor
     * Used when doctor account is deleted
     * @param doctorId The doctor ID
     */
    void deleteByAppointmentDoctorId(Long doctorId);

    /**
     * Find consultations between a specific doctor and patient
     * @param doctorId The doctor ID
     * @param patientId The patient ID
     * @return List of consultations
     */
    @Query("SELECT c FROM Consultation c WHERE c.appointment.doctor.id = :doctorId " +
            "AND c.appointment.patient.id = :patientId ORDER BY c.consultationDate DESC")
    List<Consultation> findByDoctorIdAndPatientId(
            @Param("doctorId") Long doctorId,
            @Param("patientId") Long patientId
    );
}