package com.digitalhealth.platform.billing.payment.mapper;

import com.digitalhealth.platform.billing.payment.dto.PaymentResponse;
import com.digitalhealth.platform.billing.payment.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for Payment entity to DTOs
 */
@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "appointment.id", target = "appointmentId")
    PaymentResponse toResponse(Payment payment);
}