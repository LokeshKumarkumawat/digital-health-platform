package com.digitalhealth.platform.appointment.repository;

import com.digitalhealth.platform.appointment.entity.Appointment;
import com.digitalhealth.platform.common.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Repository for Appointment entity
 * Provides database operations for appointment management
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Find all appointments for a doctor ordered by start time descending
     * @param doctorId The doctor ID
     * @return List of appointments
     */
    List<Appointment> findByDoctorIdOrderByStartTimeDesc(Long doctorId);

    /**
     * Find all appointments for a patient ordered by start time descending
     * @param patientId The patient ID
     * @return List of appointments
     */
    List<Appointment> findByPatientIdOrderByStartTimeDesc(Long patientId);

    /**
     * Find appointments by status ordered by start time descending
     * @param status The appointment status
     * @return List of appointments
     */
    List<Appointment> findByStatusOrderByStartTimeDesc(AppointmentStatus status);

    /**
     * Find conflicting appointments for a doctor in a time range
     * Checks for scheduled appointments that overlap with the proposed time slot
     *
     * @param doctorId The doctor ID
     * @param newStartTime Start of the proposed time slot
     * @param newEndTime End of the proposed time slot
     * @return List of conflicting appointments
     */
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.status = 'SCHEDULED' " +
            "AND (a.startTime < :newEndTime AND a.endTime > :newStartTime)")
    List<Appointment> findConflictingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("newStartTime") OffsetDateTime newStartTime,
            @Param("newEndTime") OffsetDateTime newEndTime
    );

    /**
     * Find upcoming appointments for a doctor
     * @param doctorId The doctor ID
     * @param fromTime Start time (typically now)
     * @return List of upcoming appointments
     */
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.startTime >= :fromTime " +
            "AND a.status = 'SCHEDULED' " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findUpcomingAppointmentsForDoctor(
            @Param("doctorId") Long doctorId,
            @Param("fromTime") OffsetDateTime fromTime
    );

    /**
     * Find upcoming appointments for a patient
     * @param patientId The patient ID
     * @param fromTime Start time (typically now)
     * @return List of upcoming appointments
     */
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patient.id = :patientId " +
            "AND a.startTime >= :fromTime " +
            "AND a.status = 'SCHEDULED' " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findUpcomingAppointmentsForPatient(
            @Param("patientId") Long patientId,
            @Param("fromTime") OffsetDateTime fromTime
    );

    /**
     * Find past appointments for a doctor
     * @param doctorId The doctor ID
     * @param toTime End time (typically now)
     * @return List of past appointments
     */
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.endTime <= :toTime " +
            "ORDER BY a.startTime DESC")
    List<Appointment> findPastAppointmentsForDoctor(
            @Param("doctorId") Long doctorId,
            @Param("toTime") OffsetDateTime toTime
    );

    /**
     * Find past appointments for a patient
     * @param patientId The patient ID
     * @param toTime End time (typically now)
     * @return List of past appointments
     */
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patient.id = :patientId " +
            "AND a.endTime <= :toTime " +
            "ORDER BY a.startTime DESC")
    List<Appointment> findPastAppointmentsForPatient(
            @Param("patientId") Long patientId,
            @Param("toTime") OffsetDateTime toTime
    );

    /**
     * Find appointments by date range
     * @param startTime Start of date range
     * @param endTime End of date range
     * @return List of appointments
     */
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.startTime >= :startTime " +
            "AND a.startTime < :endTime " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findByDateRange(
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime
    );

    /**
     * Find appointments for a doctor on a specific date
     * @param doctorId The doctor ID
     * @param startOfDay Start of the day
     * @param endOfDay End of the day
     * @return List of appointments
     */
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.startTime >= :startOfDay " +
            "AND a.startTime < :endOfDay " +
            "ORDER BY a.startTime ASC")
    List<Appointment> findDoctorAppointmentsForDate(
            @Param("doctorId") Long doctorId,
            @Param("startOfDay") OffsetDateTime startOfDay,
            @Param("endOfDay") OffsetDateTime endOfDay
    );

    /**
     * Count appointments by doctor and status
     * @param doctorId The doctor ID
     * @param status The appointment status
     * @return Count of appointments
     */
    long countByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);

    /**
     * Count appointments by patient and status
     * @param patientId The patient ID
     * @param status The appointment status
     * @return Count of appointments
     */
    long countByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    /**
     * Check if doctor has appointments in date range
     * @param doctorId The doctor ID
     * @param startTime Start time
     * @param endTime End time
     * @return true if appointments exist
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId " +
            "AND a.startTime >= :startTime " +
            "AND a.startTime < :endTime")
    boolean existsByDoctorIdAndTimeRange(
            @Param("doctorId") Long doctorId,
            @Param("startTime") OffsetDateTime startTime,
            @Param("endTime") OffsetDateTime endTime
    );

    /**
     * Find appointments needing follow-up (completed but no consultation notes)
     * @return List of appointments
     */
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.status = 'COMPLETED' " +
            "AND a.consultation IS NULL " +
            "ORDER BY a.endTime DESC")
    List<Appointment> findAppointmentsNeedingFollowUp();

    /**
     * Find appointments by doctor and status
     * @param doctorId The doctor ID
     * @param status The appointment status
     * @return List of appointments
     */
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);

    /**
     * Find appointments by patient and status
     * @param patientId The patient ID
     * @param status The appointment status
     * @return List of appointments
     */
    List<Appointment> findByPatientIdAndStatus(Long patientId, AppointmentStatus status);

    /**
     * Delete all appointments for a patient
     * Used when patient account is deleted
     * @param patientId The patient ID
     */
    void deleteByPatientId(Long patientId);

    /**
     * Delete all appointments for a doctor
     * Used when doctor account is deleted
     * @param doctorId The doctor ID
     */
    void deleteByDoctorId(Long doctorId);
}