package com.digitalhealth.platform.consultation.mapper;

import com.digitalhealth.platform.consultation.dto.ConsultationResponse;
import com.digitalhealth.platform.consultation.entity.Consultation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for Consultation entity to DTOs
 * Uses MapStruct for automatic mapping
 */
@Mapper(componentModel = "spring")
public interface ConsultationMapper {

    /**
     * Map Consultation entity to ConsultationResponse DTO
     * Includes computed fields for appointment, doctor, and patient information
     */
    @Mapping(source = "appointment.id", target = "appointmentId")
    @Mapping(source = "appointment.doctor.id", target = "doctorId")
    @Mapping(target = "doctorName",
            expression = "java(consultation.getAppointment().getDoctor().getFirstName() + \" \" + " +
                    "consultation.getAppointment().getDoctor().getLastName())")
    @Mapping(source = "appointment.patient.id", target = "patientId")
    @Mapping(target = "patientName",
            expression = "java(consultation.getAppointment().getPatient().getFirstName() + \" \" + " +
                    "consultation.getAppointment().getPatient().getLastName())")
    ConsultationResponse toResponse(Consultation consultation);
}