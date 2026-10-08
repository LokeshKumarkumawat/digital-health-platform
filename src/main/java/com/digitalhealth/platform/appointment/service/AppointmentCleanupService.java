package com.digitalhealth.platform.appointment.service;

import com.digitalhealth.platform.appointment.entity.Appointment;
import com.digitalhealth.platform.appointment.repository.AppointmentRepository;
import com.digitalhealth.platform.common.enums.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentCleanupService {

    private final AppointmentRepository appointmentRepository;

    /**
     * Runs every 5 minutes to expire pending appointments
     * Frees up slots that weren't paid for in time
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)  // Every 5 minutes
    @Transactional
    public void expirePendingAppointments() {
        OffsetDateTime now = OffsetDateTime.now();

        List<Appointment> expired = appointmentRepository
                .findByStatusAndExpiresAtBefore(AppointmentStatus.PENDING_PAYMENT, now);

        if (expired.isEmpty()) return;

        for (Appointment appointment : expired) {
            appointment.setStatus(AppointmentStatus.EXPIRED);
            log.info("Expired appointment: {} (was pending since {})",
                    appointment.getId(), appointment.getCreatedAt());
        }

        appointmentRepository.saveAll(expired);
        log.info("Expired {} pending appointments", expired.size());
    }
}