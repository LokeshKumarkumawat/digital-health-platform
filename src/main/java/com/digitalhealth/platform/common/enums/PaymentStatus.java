package com.digitalhealth.platform.common.enums;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    REQUIRES_ACTION,
    REQUIRES_CONFIRMATION,
    REQUIRES_PAYMENT_METHOD,
    SUCCEEDED,
    FAILED,
    CANCELLED,
    REFUNDED,
    DISPUTED
}