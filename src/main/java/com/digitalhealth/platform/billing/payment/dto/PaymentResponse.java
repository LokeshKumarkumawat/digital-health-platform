package com.digitalhealth.platform.billing.payment.dto;

import com.digitalhealth.platform.common.enums.PaymentMethod;
import com.digitalhealth.platform.common.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Payment response DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private String paymentIntentId;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private PaymentMethod paymentMethod;
    private String stripeChargeId;
    private String description;
    private String receiptUrl;
    private String failureReason;

    // User info
    private Long userId;
    private String userName;

    // Appointment info
    private Long appointmentId;

    private OffsetDateTime paidAt;
    private OffsetDateTime refundedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}