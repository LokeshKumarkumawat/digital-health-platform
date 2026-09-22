package com.digitalhealth.platform.billing.invoice.dto;

import com.digitalhealth.platform.common.enums.InvoiceStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Invoice response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;
    private String currency;
    private InvoiceStatus status;

    // User info
    private Long userId;
    private String userName;
    private String userEmail;

    // Appointment info
    private Long appointmentId;

    // Payment info
    private Long paymentId;

    private String description;
    private String lineItems;

    private OffsetDateTime issueDate;
    private OffsetDateTime dueDate;
    private OffsetDateTime paidDate;

    private String notes;
    private String pdfUrl;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}