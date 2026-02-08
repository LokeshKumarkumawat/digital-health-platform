package com.digitalhealth.platform.billing.payment.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Response containing payment intent details
 * Sent to frontend for Stripe.js integration
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntentResponse {

    private String paymentIntentId;
    private String clientSecret; // For Stripe.js on frontend
    private BigDecimal amount;
    private String currency;
    private String status;
    private String description;
    private Long appointmentId;
}