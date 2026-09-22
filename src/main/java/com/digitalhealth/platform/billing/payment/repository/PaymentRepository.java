package com.digitalhealth.platform.billing.payment.repository;

import com.digitalhealth.platform.billing.payment.entity.Payment;
import com.digitalhealth.platform.common.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Payment entity
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payment by Stripe payment intent ID
     */
    Optional<Payment> findByPaymentIntentId(String paymentIntentId);

    /**
     * Find payment by Stripe charge ID
     */
    Optional<Payment> findByStripeChargeId(String stripeChargeId);

    /**
     * Find payment by appointment ID
     */
    Optional<Payment> findByAppointmentId(Long appointmentId);

    /**
     * Find all payments for a user
     */
    List<Payment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find payments by status
     */
    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);

    /**
     * Find payments by user and status
     */
    List<Payment> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, PaymentStatus status);

    /**
     * Find successful payments for a user
     */
    @Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.status = 'SUCCEEDED' " +
            "ORDER BY p.paidAt DESC")
    List<Payment> findSuccessfulPaymentsByUserId(@Param("userId") Long userId);

    /**
     * Find payments within date range
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate " +
            "ORDER BY p.createdAt DESC")
    List<Payment> findByDateRange(
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    /**
     * Count payments by user
     */
    long countByUserId(Long userId);

    /**
     * Count successful payments by user
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.user.id = :userId AND p.status = 'SUCCEEDED'")
    long countSuccessfulPaymentsByUserId(@Param("userId") Long userId);

    /**
     * Check if payment exists for appointment
     */
    boolean existsByAppointmentId(Long appointmentId);

    /**
     * Find pending/processing payments older than specified time
     * For cleanup/timeout operations
     */
    @Query("SELECT p FROM Payment p WHERE (p.status = 'PENDING' OR p.status = 'PROCESSING') " +
            "AND p.createdAt < :cutoffTime")
    List<Payment> findStalePayments(@Param("cutoffTime") OffsetDateTime cutoffTime);
}