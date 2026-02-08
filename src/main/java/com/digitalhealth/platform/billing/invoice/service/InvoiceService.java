package com.digitalhealth.platform.billing.invoice.service;

import com.digitalhealth.platform.appointment.entity.Appointment;
import com.digitalhealth.platform.appointment.repository.AppointmentRepository;
import com.digitalhealth.platform.billing.invoice.dto.*;
import com.digitalhealth.platform.billing.invoice.entity.Invoice;
import com.digitalhealth.platform.billing.invoice.mapper.InvoiceMapper;
import com.digitalhealth.platform.billing.invoice.repository.InvoiceRepository;
import com.digitalhealth.platform.billing.payment.entity.Payment;
import com.digitalhealth.platform.billing.payment.repository.PaymentRepository;
import com.digitalhealth.platform.common.enums.InvoiceStatus;
import com.digitalhealth.platform.common.exception.BadRequestException;
import com.digitalhealth.platform.common.exception.ResourceNotFoundException;
import com.digitalhealth.platform.common.exception.UnauthorizedException;
import com.digitalhealth.platform.users.entity.User;
import com.digitalhealth.platform.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final InvoiceMapper invoiceMapper;

    /**
     * Create invoice for appointment or other service
     */
    @Transactional
    public InvoiceResponse createInvoice(InvoiceCreateRequest request) {
        log.info("Creating invoice for userId: {}", request.getUserId());

        // Validate user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // Validate appointment if provided
        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Appointment not found with id: " + request.getAppointmentId()));

            // Check if invoice already exists for this appointment
            if (invoiceRepository.existsByAppointmentId(request.getAppointmentId())) {
                throw new BadRequestException("Invoice already exists for this appointment");
            }
        }

        // Calculate total
        var total = request.getSubtotal().add(request.getTax());

        // Generate invoice number
        String invoiceNumber = generateInvoiceNumber();

        // Set due date if not provided (default: 30 days from now)
        OffsetDateTime dueDate = request.getDueDate() != null
                ? request.getDueDate()
                : OffsetDateTime.now().plusDays(30);

        // Create invoice
        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .subtotal(request.getSubtotal())
                .tax(request.getTax())
                .total(total)
                .currency(request.getCurrency().toUpperCase())
                .status(InvoiceStatus.DRAFT)
                .user(user)
                .appointment(appointment)
                .description(request.getDescription())
                .lineItems(request.getLineItems())
                .issueDate(OffsetDateTime.now())
                .dueDate(dueDate)
                .notes(request.getNotes())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice created: {}", invoiceNumber);

        return invoiceMapper.toResponse(savedInvoice);
    }

    /**
     * Send invoice to customer (mark as PENDING)
     */
    @Transactional
    public InvoiceResponse sendInvoice(Long invoiceId) {
        log.info("Sending invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new BadRequestException("Only draft invoices can be sent");
        }

        invoice.setStatus(InvoiceStatus.PENDING);
        Invoice updatedInvoice = invoiceRepository.save(invoice);

        // TODO: Send email to customer with invoice
        log.info("Invoice sent: {}", invoice.getInvoiceNumber());

        return invoiceMapper.toResponse(updatedInvoice);
    }

    /**
     * Mark invoice as paid
     * Links to payment record
     */
    @Transactional
    public InvoiceResponse markInvoiceAsPaid(Long invoiceId, Long paymentId) {
        log.info("Marking invoice {} as paid with payment {}", invoiceId, paymentId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        // Validate payment is successful
        if (payment.getStatus() != com.digitalhealth.platform.common.enums.PaymentStatus.SUCCEEDED) {
            throw new BadRequestException("Payment must be successful to mark invoice as paid");
        }

        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPayment(payment);
        invoice.setPaidDate(OffsetDateTime.now());

        Invoice updatedInvoice = invoiceRepository.save(invoice);

        return invoiceMapper.toResponse(updatedInvoice);
    }

    /**
     * Get invoice by ID
     */
    public InvoiceResponse getInvoiceById(Long invoiceId) {
        log.debug("Fetching invoice with id: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        validateUserAccessToInvoice(invoice);

        return invoiceMapper.toResponse(invoice);
    }

    /**
     * Get invoice by invoice number
     */
    public InvoiceResponse getInvoiceByNumber(String invoiceNumber) {
        log.debug("Fetching invoice with number: {}", invoiceNumber);

        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found with number: " + invoiceNumber));

        validateUserAccessToInvoice(invoice);

        return invoiceMapper.toResponse(invoice);
    }

    /**
     * Get invoice by appointment ID
     */
    public InvoiceResponse getInvoiceByAppointmentId(Long appointmentId) {
        log.debug("Fetching invoice for appointmentId: {}", appointmentId);

        Invoice invoice = invoiceRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found for appointment id: " + appointmentId));

        validateUserAccessToInvoice(invoice);

        return invoiceMapper.toResponse(invoice);
    }

    /**
     * Get all invoices for current user
     */
    public List<InvoiceResponse> getMyInvoices() {
        log.debug("Fetching invoices for current user");

        User currentUser = getCurrentAuthenticatedUser();

        List<Invoice> invoices = invoiceRepository.findByUserIdOrderByIssueDateDesc(currentUser.getId());

        return invoices.stream()
                .map(invoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get unpaid invoices for current user
     */
    public List<InvoiceResponse> getMyUnpaidInvoices() {
        log.debug("Fetching unpaid invoices for current user");

        User currentUser = getCurrentAuthenticatedUser();

        List<Invoice> invoices = invoiceRepository.findUnpaidInvoicesByUserId(currentUser.getId());

        return invoices.stream()
                .map(invoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all invoices (admin only)
     */
    public List<InvoiceResponse> getAllInvoices() {
        log.debug("Fetching all invoices");

        return invoiceRepository.findAll().stream()
                .map(invoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get invoices by status (admin only)
     */
    public List<InvoiceResponse> getInvoicesByStatus(InvoiceStatus status) {
        log.debug("Fetching invoices with status: {}", status);

        return invoiceRepository.findByStatusOrderByIssueDateDesc(status).stream()
                .map(invoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get overdue invoices (admin only)
     */
    public List<InvoiceResponse> getOverdueInvoices() {
        log.debug("Fetching overdue invoices");

        return invoiceRepository.findOverdueInvoices(OffsetDateTime.now()).stream()
                .map(invoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get invoices within date range (admin only)
     */
    public List<InvoiceResponse> getInvoicesByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        log.debug("Fetching invoices between {} and {}", startDate, endDate);

        if (endDate.isBefore(startDate)) {
            throw new BadRequestException("End date must be after start date");
        }

        return invoiceRepository.findByDateRange(startDate, endDate).stream()
                .map(invoiceMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cancel invoice
     */
    @Transactional
    public void cancelInvoice(Long invoiceId) {
        log.info("Cancelling invoice: {}", invoiceId);

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        // Only draft or pending invoices can be cancelled
        if (invoice.getStatus() != InvoiceStatus.DRAFT && invoice.getStatus() != InvoiceStatus.PENDING) {
            throw new BadRequestException("Only draft or pending invoices can be cancelled");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);

        log.info("Invoice cancelled: {}", invoice.getInvoiceNumber());
    }

    /**
     * Mark overdue invoices
     * Should be run as scheduled task
     */
    @Transactional
    public int markOverdueInvoices() {
        log.info("Checking for overdue invoices");

        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(OffsetDateTime.now());

        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
        }

        if (!overdueInvoices.isEmpty()) {
            invoiceRepository.saveAll(overdueInvoices);
            log.info("Marked {} invoices as overdue", overdueInvoices.size());
        }

        return overdueInvoices.size();
    }

    // ==================== PRIVATE HELPER METHODS ====================

    /**
     * Generate unique invoice number
     * Format: INV-YYYY-NNNN (e.g., INV-2026-0001)
     */
    private String generateInvoiceNumber() {
        int currentYear = Year.now().getValue();
        String yearPrefix = "INV-" + currentYear + "-";

        String latestInvoiceNumber = invoiceRepository
                .findLatestInvoiceNumberForYear(yearPrefix + "%")
                .orElse(null);

        int nextNumber = 1;
        if (latestInvoiceNumber != null) {
            String numberPart = latestInvoiceNumber.substring(latestInvoiceNumber.lastIndexOf("-") + 1);
            nextNumber = Integer.parseInt(numberPart) + 1;
        }

        return String.format("%s%04d", yearPrefix, nextNumber);
    }

    /**
     * Validate user has access to invoice
     */
    private void validateUserAccessToInvoice(Invoice invoice) {
        User currentUser = getCurrentAuthenticatedUser();

        boolean isOwner = invoice.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You do not have permission to access this invoice");
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