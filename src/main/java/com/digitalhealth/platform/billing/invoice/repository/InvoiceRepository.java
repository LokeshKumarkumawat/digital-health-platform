package com.digitalhealth.platform.billing.invoice.repository;

import com.digitalhealth.platform.billing.invoice.entity.Invoice;
import com.digitalhealth.platform.common.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;


public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);


    Optional<Invoice> findByAppointmentId(Long appointmentId);


    Optional<Invoice> findByPaymentId(Long paymentId);


    List<Invoice> findByUserIdOrderByIssueDateDesc(Long userId);


    List<Invoice> findByStatusOrderByIssueDateDesc(InvoiceStatus status);


    List<Invoice> findByUserIdAndStatusOrderByIssueDateDesc(Long userId, InvoiceStatus status);


    @Query("SELECT i FROM Invoice i WHERE (i.status = 'PENDING' OR i.status = 'PARTIALLY_PAID') " +
            "AND i.dueDate < :currentDate ORDER BY i.dueDate ASC")
    List<Invoice> findOverdueInvoices(@Param("currentDate") OffsetDateTime currentDate);


    @Query("SELECT i FROM Invoice i WHERE i.issueDate >= :startDate AND i.issueDate <= :endDate " +
            "ORDER BY i.issueDate DESC")
    List<Invoice> findByDateRange(
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate
    );

    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId " +
            "AND (i.status = 'PENDING' OR i.status = 'PARTIALLY_PAID' OR i.status = 'OVERDUE') " +
            "ORDER BY i.dueDate ASC")
    List<Invoice> findUnpaidInvoicesByUserId(@Param("userId") Long userId);

    long countByUserId(Long userId);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.user.id = :userId " +
            "AND (i.status = 'PENDING' OR i.status = 'PARTIALLY_PAID' OR i.status = 'OVERDUE')")
    long countUnpaidInvoicesByUserId(@Param("userId") Long userId);

    boolean existsByAppointmentId(Long appointmentId);

    @Query("SELECT i.invoiceNumber FROM Invoice i WHERE i.invoiceNumber LIKE :yearPrefix " +
            "ORDER BY i.invoiceNumber DESC LIMIT 1")
    Optional<String> findLatestInvoiceNumberForYear(@Param("yearPrefix") String yearPrefix);
}
