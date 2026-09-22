package com.digitalhealth.platform.billing.invoice.entity;

import com.digitalhealth.platform.appointment.entity.Appointment;
import com.digitalhealth.platform.billing.payment.entity.Payment;
import com.digitalhealth.platform.common.enums.InvoiceStatus;
import com.digitalhealth.platform.users.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "invoices_seq_gen")
    @SequenceGenerator(name = "invoices_seq_gen", sequenceName = "invoices_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    private String invoiceNumber; // Auto-generated: INV-2026-0001

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Column(nullable = false)
    private BigDecimal tax; // Tax amount

    @Column(nullable = false)
    private BigDecimal total; // Subtotal + Tax

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Patient

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String lineItems; // JSON array of line items

    private OffsetDateTime issueDate;

    private OffsetDateTime dueDate;

    private OffsetDateTime paidDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(columnDefinition = "TEXT")
    private String pdfUrl; // URL to generated PDF invoice

    @Version
    private Long version;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
        if (issueDate == null) {
            issueDate = OffsetDateTime.now();
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}