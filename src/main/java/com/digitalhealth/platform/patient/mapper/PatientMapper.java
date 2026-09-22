package com.digitalhealth.platform.patient.mapper;

import com.digitalhealth.platform.patient.dto.PatientResponse;
import com.digitalhealth.platform.patient.dto.PatientSummaryResponse;
import com.digitalhealth.platform.patient.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "fullName",
            expression = "java(patient.getFirstName() + \" \" + patient.getLastName())")
    @Mapping(target = "age",
            expression = "java(java.time.Period.between(patient.getDateOfBirth(), java.time.LocalDate.now()).getYears())")
    PatientSummaryResponse toSummary(Patient patient);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "age",
            expression = "java(java.time.Period.between(patient.getDateOfBirth(), java.time.LocalDate.now()).getYears())")
    PatientResponse toResponse(Patient patient);
}
