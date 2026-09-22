package com.digitalhealth.platform.billing.payment.entity;

import com.digitalhealth.platform.appointment.entity.Appointment;
import com.digitalhealth.platform.common.enums.PaymentMethod;
import com.digitalhealth.platform.common.enums.PaymentStatus;
import com.digitalhealth.platform.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payments_seq_gen")
    @SequenceGenerator(name = "payments_seq_gen", sequenceName = "payments_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String paymentIntentId; // Stripe Payment Intent ID

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency; // USD, EUR, etc.

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(unique = true)
    private String stripeChargeId; // Stripe Charge ID after successful payment

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String receiptUrl; // Stripe receipt URL

    @Column(columnDefinition = "TEXT")
    private String failureReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Patient who made the payment

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment; // Link to appointment (nullable for other payment types)

    private OffsetDateTime paidAt; // When payment was successful

    private OffsetDateTime refundedAt; // When payment was refunded

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON string for additional data

    @Version
    private Long version;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}