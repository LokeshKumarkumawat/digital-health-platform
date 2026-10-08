package com.digitalhealth.platform.billing.payment.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Address line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country; // ISO 2-letter code (IN, US, etc.)
}