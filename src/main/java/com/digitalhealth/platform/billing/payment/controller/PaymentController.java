package com.digitalhealth.platform.billing.payment.controller;

import com.digitalhealth.platform.billing.payment.dto.*;
import com.digitalhealth.platform.billing.payment.service.PaymentService;
import com.digitalhealth.platform.common.enums.PaymentStatus;
import com.digitalhealth.platform.common.response.ApiResponse;
import com.stripe.exception.StripeException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * REST Controller for Payment Management
 *
 * Endpoints:
 * - POST   /api/v1/payments/intent              - Create payment intent (PATIENT)
 * - POST   /api/v1/payments/confirm/{id}        - Confirm payment
 * - POST   /api/v1/payments/{id}/refund         - Refund payment (ADMIN)
 * - GET    /api/v1/payments/{id}                - Get payment by ID
 * - GET    /api/v1/payments/intent/{intentId}   - Get payment by intent ID
 * - GET    /api/v1/payments/appointment/{id}    - Get payment for appointment
 * - GET    /api/v1/payments/my-payments         - Get current user's payments
 * - GET    /api/v1/payments/my-successful       - Get current user's successful payments
 * - GET    /api/v1/payments                     - Get all payments (ADMIN)
 * - GET    /api/v1/payments/status/{status}     - Get payments by status (ADMIN)
 * - GET    /api/v1/payments/date-range          - Get payments by date range (ADMIN)
 * - DELETE /api/v1/payments/{id}/cancel         - Cancel payment
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Create payment intent for appointment
     * Patient initiates payment for their appointment
     */
    @PostMapping("/intent")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<ApiResponse<PaymentIntentResponse>> createPaymentIntent(
            @Valid @RequestBody PaymentIntentCreateRequest request) throws StripeException {

        PaymentIntentResponse response = paymentService.createPaymentIntent(request);

        ApiResponse<PaymentIntentResponse> apiResponse = ApiResponse.<PaymentIntentResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Payment intent created successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Confirm payment after Stripe processes it
     * Called after frontend confirms payment with Stripe
     */
    @PostMapping("/confirm/{paymentIntentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @PathVariable String paymentIntentId) throws StripeException {

        PaymentResponse response = paymentService.confirmPayment(paymentIntentId);

        ApiResponse<PaymentResponse> apiResponse = ApiResponse.<PaymentResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Payment confirmed successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Refund a payment
     * Admin only
     */
    @PostMapping("/{paymentId}/refund")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(
            @PathVariable Long paymentId,
            @RequestParam BigDecimal refundAmount,
            @RequestParam(required = false) String reason) throws StripeException {

        PaymentResponse response = paymentService.refundPayment(paymentId, refundAmount, reason);

        ApiResponse<PaymentResponse> apiResponse = ApiResponse.<PaymentResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Payment refunded successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get payment by ID
     * Accessible by payment owner or admin
     */
    @GetMapping("/{paymentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Long paymentId) {

        PaymentResponse response = paymentService.getPaymentById(paymentId);

        ApiResponse<PaymentResponse> apiResponse = ApiResponse.<PaymentResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Payment retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get payment by payment intent ID
     */
    @GetMapping("/intent/{paymentIntentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByIntentId(
            @PathVariable String paymentIntentId) {

        PaymentResponse response = paymentService.getPaymentByIntentId(paymentIntentId);

        ApiResponse<PaymentResponse> apiResponse = ApiResponse.<PaymentResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Payment retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get payment for an appointment
     */
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByAppointmentId(
            @PathVariable Long appointmentId) {

        PaymentResponse response = paymentService.getPaymentByAppointmentId(appointmentId);

        ApiResponse<PaymentResponse> apiResponse = ApiResponse.<PaymentResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Payment retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all payments for current user
     */
    @GetMapping("/my-payments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPayments() {

        List<PaymentResponse> payments = paymentService.getMyPayments();

        ApiResponse<List<PaymentResponse>> apiResponse = ApiResponse.<List<PaymentResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your payments retrieved successfully")
                .data(payments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get successful payments for current user
     */
    @GetMapping("/my-successful")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMySuccessfulPayments() {

        List<PaymentResponse> payments = paymentService.getMySuccessfulPayments();

        ApiResponse<List<PaymentResponse>> apiResponse = ApiResponse.<List<PaymentResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your successful payments retrieved successfully")
                .data(payments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all payments
     * Admin only
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getAllPayments() {

        List<PaymentResponse> payments = paymentService.getAllPayments();

        ApiResponse<List<PaymentResponse>> apiResponse = ApiResponse.<List<PaymentResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All payments retrieved successfully")
                .data(payments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get payments by status
     * Admin only
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByStatus(
            @PathVariable PaymentStatus status) {

        List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);

        ApiResponse<List<PaymentResponse>> apiResponse = ApiResponse.<List<PaymentResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Payments with status " + status + " retrieved successfully")
                .data(payments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get payments within date range
     * Admin only
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {

        List<PaymentResponse> payments = paymentService.getPaymentsByDateRange(startDate, endDate);

        ApiResponse<List<PaymentResponse>> apiResponse = ApiResponse.<List<PaymentResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Payments retrieved successfully")
                .data(payments)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Cancel payment
     * Can be done by payment owner before processing
     */
    @DeleteMapping("/{paymentId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(@PathVariable Long paymentId)
            throws StripeException {

        paymentService.cancelPayment(paymentId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Payment cancelled successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}