package com.digitalhealth.platform.doctor.mapper;

import com.digitalhealth.platform.doctor.dto.DoctorResponse;
import com.digitalhealth.platform.doctor.dto.DoctorSummaryResponse;
import com.digitalhealth.platform.doctor.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "fullName", expression = "java(doctor.getFirstName() + \" \" + doctor.getLastName())")
    DoctorSummaryResponse toSummary(Doctor doctor);

    @Mapping(source = "user.id", target = "userId")
    DoctorResponse toResponse(Doctor doctor);
}
