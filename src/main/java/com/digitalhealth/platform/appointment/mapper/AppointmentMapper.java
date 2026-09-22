package com.digitalhealth.platform.appointment.mapper;

import com.digitalhealth.platform.appointment.dto.AppointmentResponse;
import com.digitalhealth.platform.appointment.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for Appointment entity to DTOs
 * Uses MapStruct for automatic mapping
 */
@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    /**
     * Map Appointment entity to AppointmentResponse DTO
     * Includes computed fields for doctor and patient information
     */
    @Mapping(source = "doctor.id", target = "doctorId")
    @Mapping(target = "doctorName",
            expression = "java(appointment.getDoctor().getFirstName() + \" \" + appointment.getDoctor().getLastName())")
    @Mapping(source = "doctor.specialization", target = "specialization")
    @Mapping(source = "patient.id", target = "patientId")
    @Mapping(target = "patientName",
            expression = "java(appointment.getPatient().getFirstName() + \" \" + appointment.getPatient().getLastName())")
    AppointmentResponse toResponse(Appointment appointment);
}
