package com.digitalhealth.platform.billing.payment.service;

import com.digitalhealth.platform.appointment.entity.Appointment;
import com.digitalhealth.platform.appointment.repository.AppointmentRepository;
import com.digitalhealth.platform.billing.payment.dto.*;
import com.digitalhealth.platform.billing.payment.entity.Payment;
import com.digitalhealth.platform.billing.payment.mapper.PaymentMapper;
import com.digitalhealth.platform.billing.payment.repository.PaymentRepository;
import com.digitalhealth.platform.common.enums.PaymentStatus;
import com.digitalhealth.platform.common.exception.BadRequestException;
import com.digitalhealth.platform.common.exception.ResourceNotFoundException;
import com.digitalhealth.platform.common.exception.UnauthorizedException;
import com.digitalhealth.platform.users.entity.User;
import com.digitalhealth.platform.users.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    /**
     * Create payment intent for appointment payment
     * This is called when patient wants to pay for an appointment
     */
    @Transactional
    public PaymentIntentResponse createPaymentIntent(PaymentIntentCreateRequest request) throws StripeException {
        log.info("Creating payment intent for appointmentId: {}", request.getAppointmentId());

        User currentUser = getCurrentAuthenticatedUser();

        // Validate appointment exists
        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Appointment not found with id: " + request.getAppointmentId()));

        // Security: Only the patient can create payment for their appointment
        if (!appointment.getPatient().getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only create payment for your own appointments");
        }

        // Check if payment already exists for this appointment
        if (paymentRepository.existsByAppointmentId(request.getAppointmentId())) {
            throw new BadRequestException("Payment already exists for this appointment");
        }

        // Convert amount to cents (Stripe uses smallest currency unit)
        long amountInCents = request.getAmount().multiply(new BigDecimal(100)).longValue();

        // Create metadata
        Map<String, String> metadata = new HashMap<>();
        metadata.put("appointmentId", request.getAppointmentId().toString());
        metadata.put("userId", currentUser.getId().toString());
        metadata.put("userName", currentUser.getName());

        // Build Stripe payment intent params
        PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(request.getCurrency().toLowerCase())
                .putAllMetadata(metadata);

        if (request.getDescription() != null) {
            paramsBuilder.setDescription(request.getDescription());
        }

        if (request.getPaymentMethodId() != null) {
            paramsBuilder.setPaymentMethod(request.getPaymentMethodId());
        }

        // Create Stripe payment intent
        PaymentIntent paymentIntent = PaymentIntent.create(paramsBuilder.build());

        // Save payment record in database
        Payment payment = Payment.builder()
                .paymentIntentId(paymentIntent.getId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .status(PaymentStatus.PENDING)
                .description(request.getDescription())
                .user(currentUser)
                .appointment(appointment)
                .build();

        paymentRepository.save(payment);

        log.info("Payment intent created: {}", paymentIntent.getId());

        // Return response with client secret for frontend
        return PaymentIntentResponse.builder()
                .paymentIntentId(paymentIntent.getId())
                .clientSecret(paymentIntent.getClientSecret())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(paymentIntent.getStatus())
                .description(request.getDescription())
                .appointmentId(request.getAppointmentId())
                .build();
    }

    /**
     * Confirm payment after Stripe processes it
     * Called by webhook or after frontend confirmation
     */
    @Transactional
    public PaymentResponse confirmPayment(String paymentIntentId) throws StripeException {
        log.info("Confirming payment: {}", paymentIntentId);

        Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with paymentIntentId: " + paymentIntentId));

        // Retrieve payment intent from Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        // Update payment status based on Stripe status
        payment.setStatus(mapStripeStatus(paymentIntent.getStatus()));

        if ("succeeded".equals(paymentIntent.getStatus())) {
            payment.setPaidAt(OffsetDateTime.now());

            String latestChargeId = paymentIntent.getLatestCharge();
            if (latestChargeId != null) {
                Charge charge = Charge.retrieve(latestChargeId);

                payment.setStripeChargeId(charge.getId());
                payment.setReceiptUrl(charge.getReceiptUrl());
            }
//             Stripe SDK < 22
//            if (paymentIntent.getCharges() != null && paymentIntent.getCharges().getData().size() > 0) {
//                payment.setStripeChargeId(paymentIntent.getCharges().getData().get(0).getId());
//                payment.setReceiptUrl(paymentIntent.getCharges().getData().get(0).getReceiptUrl());
//            }

            // TODO: Send email receipt to user
            log.info("Payment succeeded: {}", paymentIntentId);
        } else if ("payment_failed".equals(paymentIntent.getStatus())) {
            payment.setFailureReason(paymentIntent.getLastPaymentError() != null
                    ? paymentIntent.getLastPaymentError().getMessage()
                    : "Unknown error");
            log.warn("Payment failed: {}", paymentIntentId);
        }

        Payment updatedPayment = paymentRepository.save(payment);

        return paymentMapper.toResponse(updatedPayment);
    }

    /**
     * Process refund for a payment
     */
    @Transactional
    public PaymentResponse refundPayment(Long paymentId, BigDecimal refundAmount, String reason)
            throws StripeException {
        log.info("Processing refund for paymentId: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        // Only successful payments can be refunded
        if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
            throw new BadRequestException("Only successful payments can be refunded");
        }

        // Validate refund amount
        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new BadRequestException("Refund amount cannot exceed payment amount");
        }

        // Create refund in Stripe
        long refundAmountInCents = refundAmount.multiply(new BigDecimal(100)).longValue();

        RefundCreateParams.Builder builder = RefundCreateParams.builder()
                .setPaymentIntent(payment.getPaymentIntentId())
                .setAmount(refundAmountInCents)
                .setReason(RefundCreateParams.Reason.REQUESTED_BY_CUSTOMER);

        if (reason != null && !reason.isBlank()) {
            builder.putMetadata("reason", reason);
        }
        RefundCreateParams params = builder.build();

        Refund refund = Refund.create(params);

        // Update payment status
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(OffsetDateTime.now());
        payment.setFailureReason(reason);

        Payment updatedPayment = paymentRepository.save(payment);

        log.info("Refund processed: {}", refund.getId());

        return paymentMapper.toResponse(updatedPayment);
    }

    /**
     * Get payment by ID
     */
    public PaymentResponse getPaymentById(Long paymentId) {
        log.debug("Fetching payment with id: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        // Validate user has access
        validateUserAccessToPayment(payment);

        return paymentMapper.toResponse(payment);
    }

    /**
     * Get payment by payment intent ID
     */
    public PaymentResponse getPaymentByIntentId(String paymentIntentId) {
        log.debug("Fetching payment with intentId: {}", paymentIntentId);

        Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found with paymentIntentId: " + paymentIntentId));

        validateUserAccessToPayment(payment);

        return paymentMapper.toResponse(payment);
    }

    /**
     * Get payment for an appointment
     */
    public PaymentResponse getPaymentByAppointmentId(Long appointmentId) {
        log.debug("Fetching payment for appointmentId: {}", appointmentId);

        Payment payment = paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for appointment id: " + appointmentId));

        validateUserAccessToPayment(payment);

        return paymentMapper.toResponse(payment);
    }

    /**
     * Get all payments for current user
     */
    public List<PaymentResponse> getMyPayments() {
        log.debug("Fetching payments for current user");

        User currentUser = getCurrentAuthenticatedUser();

        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());

        return payments.stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get successful payments for current user
     */
    public List<PaymentResponse> getMySuccessfulPayments() {
        log.debug("Fetching successful payments for current user");

        User currentUser = getCurrentAuthenticatedUser();

        List<Payment> payments = paymentRepository.findSuccessfulPaymentsByUserId(currentUser.getId());

        return payments.stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all payments (admin only)
     */
    public List<PaymentResponse> getAllPayments() {
        log.debug("Fetching all payments");

        return paymentRepository.findAll().stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get payments by status (admin only)
     */
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        log.debug("Fetching payments with status: {}", status);

        return paymentRepository.findByStatusOrderByCreatedAtDesc(status).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get payments within date range (admin only)
     */
    public List<PaymentResponse> getPaymentsByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        log.debug("Fetching payments between {} and {}", startDate, endDate);

        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date must be after start date");
        }

        return paymentRepository.findByDateRange(startDate, endDate).stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cancel payment (before it's processed)
     */
    @Transactional
    public void cancelPayment(Long paymentId) throws StripeException {
        log.info("Cancelling payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        // Validate user has permission
        validateUserAccessToPayment(payment);

        // Only pending/processing payments can be cancelled
        if (payment.getStatus() != PaymentStatus.PENDING &&
                payment.getStatus() != PaymentStatus.PROCESSING) {
            throw new BadRequestException("Only pending payments can be cancelled");
        }

        // Cancel in Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(payment.getPaymentIntentId());
        paymentIntent.cancel();

        // Update status
        payment.setStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        log.info("Payment cancelled: {}", paymentId);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Map Stripe payment intent status to our PaymentStatus enum
     */
    private PaymentStatus mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "requires_payment_method" -> PaymentStatus.REQUIRES_PAYMENT_METHOD;
            case "requires_confirmation" -> PaymentStatus.REQUIRES_CONFIRMATION;
            case "requires_action" -> PaymentStatus.REQUIRES_ACTION;
            case "processing" -> PaymentStatus.PROCESSING;
            case "succeeded" -> PaymentStatus.SUCCEEDED;
            case "canceled" -> PaymentStatus.CANCELLED;
            default -> PaymentStatus.FAILED;
        };
    }

    /**
     * Validate user has access to payment
     */
    private void validateUserAccessToPayment(Payment payment) {
        User currentUser = getCurrentAuthenticatedUser();

        boolean isOwner = payment.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You do not have permission to access this payment");
        }
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("User not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}