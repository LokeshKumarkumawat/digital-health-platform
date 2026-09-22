package com.digitalhealth.platform.billing.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

/**
 * Request to create a payment intent
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntentCreateRequest {

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private String currency; // USD, EUR, etc.

    private String description;

    @NotNull
    private Long appointmentId; // Link to appointment

    private String paymentMethodId; // Stripe payment method ID (if already created)

    private Boolean savePaymentMethod; // Save for future use
}