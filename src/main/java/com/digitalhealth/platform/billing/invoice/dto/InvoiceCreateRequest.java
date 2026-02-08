package com.digitalhealth.platform.billing.invoice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Request to create an invoice
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceCreateRequest {

    @NotNull
    @Positive
    private BigDecimal subtotal;

    @NotNull
    private BigDecimal tax; // Can be 0

    @NotNull
    private String currency;

    @NotNull
    private Long userId; // Patient

    private Long appointmentId; // Optional: link to appointment

    private String description;

    private String lineItems; // JSON array of line items

    private OffsetDateTime dueDate;

    private String notes;
}