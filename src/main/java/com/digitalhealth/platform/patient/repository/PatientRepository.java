package com.digitalhealth.platform.patient.repository;

import com.digitalhealth.platform.patient.entity.Patient;
import com.digitalhealth.platform.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUser(User user);
}
