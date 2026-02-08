package com.digitalhealth.platform.billing.invoice.mapper;

import com.digitalhealth.platform.billing.invoice.dto.InvoiceResponse;
import com.digitalhealth.platform.billing.invoice.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "user.email", target = "userEmail")
    @Mapping(source = "appointment.id", target = "appointmentId")
    @Mapping(source = "payment.id", target = "paymentId")
    InvoiceResponse toResponse(Invoice invoice);
}