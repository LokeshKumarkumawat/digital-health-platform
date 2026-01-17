package com.digitalhealth.platform.doctor.repository;

import com.digitalhealth.platform.common.enums.Specialization;
import com.digitalhealth.platform.doctor.entity.Doctor;
import com.digitalhealth.platform.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
    List<Doctor> findBySpecialization(Specialization specialization);
}