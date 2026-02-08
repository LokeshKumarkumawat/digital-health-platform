package com.digitalhealth.platform.billing.invoice.controller;

import com.digitalhealth.platform.billing.invoice.dto.*;
import com.digitalhealth.platform.billing.invoice.service.InvoiceService;
import com.digitalhealth.platform.common.enums.InvoiceStatus;
import com.digitalhealth.platform.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * REST Controller for Invoice Management
 *
 * Endpoints:
 * - POST   /api/v1/invoices                     - Create invoice (ADMIN)
 * - POST   /api/v1/invoices/{id}/send           - Send invoice to customer (ADMIN)
 * - POST   /api/v1/invoices/{id}/mark-paid      - Mark invoice as paid (ADMIN)
 * - GET    /api/v1/invoices/{id}                - Get invoice by ID
 * - GET    /api/v1/invoices/number/{number}     - Get invoice by invoice number
 * - GET    /api/v1/invoices/appointment/{id}    - Get invoice by appointment ID
 * - GET    /api/v1/invoices/my-invoices         - Get current user's invoices
 * - GET    /api/v1/invoices/my-unpaid           - Get current user's unpaid invoices
 * - GET    /api/v1/invoices                     - Get all invoices (ADMIN)
 * - GET    /api/v1/invoices/status/{status}     - Get invoices by status (ADMIN)
 * - GET    /api/v1/invoices/overdue             - Get overdue invoices (ADMIN)
 * - GET    /api/v1/invoices/date-range          - Get invoices by date range (ADMIN)
 * - DELETE /api/v1/invoices/{id}/cancel         - Cancel invoice (ADMIN)
 */
@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    /**
     * Create invoice
     * Admin creates invoice for appointment or service
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> createInvoice(
            @Valid @RequestBody InvoiceCreateRequest request) {

        InvoiceResponse response = invoiceService.createInvoice(request);

        ApiResponse<InvoiceResponse> apiResponse = ApiResponse.<InvoiceResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Invoice created successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    /**
     * Send invoice to customer
     * Changes status from DRAFT to PENDING and sends email
     */
    @PostMapping("/{invoiceId}/send")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> sendInvoice(
            @PathVariable Long invoiceId) {

        InvoiceResponse response = invoiceService.sendInvoice(invoiceId);

        ApiResponse<InvoiceResponse> apiResponse = ApiResponse.<InvoiceResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Invoice sent successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Mark invoice as paid
     * Links invoice to payment record
     */
    @PostMapping("/{invoiceId}/mark-paid")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<InvoiceResponse>> markInvoiceAsPaid(
            @PathVariable Long invoiceId,
            @RequestParam Long paymentId) {

        InvoiceResponse response = invoiceService.markInvoiceAsPaid(invoiceId, paymentId);

        ApiResponse<InvoiceResponse> apiResponse = ApiResponse.<InvoiceResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Invoice marked as paid successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get invoice by ID
     * Accessible by invoice owner or admin
     */
    @GetMapping("/{invoiceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceById(
            @PathVariable Long invoiceId) {

        InvoiceResponse response = invoiceService.getInvoiceById(invoiceId);

        ApiResponse<InvoiceResponse> apiResponse = ApiResponse.<InvoiceResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Invoice retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get invoice by invoice number
     */
    @GetMapping("/number/{invoiceNumber}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceByNumber(
            @PathVariable String invoiceNumber) {

        InvoiceResponse response = invoiceService.getInvoiceByNumber(invoiceNumber);

        ApiResponse<InvoiceResponse> apiResponse = ApiResponse.<InvoiceResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Invoice retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get invoice by appointment ID
     */
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<InvoiceResponse>> getInvoiceByAppointmentId(
            @PathVariable Long appointmentId) {

        InvoiceResponse response = invoiceService.getInvoiceByAppointmentId(appointmentId);

        ApiResponse<InvoiceResponse> apiResponse = ApiResponse.<InvoiceResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Invoice retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all invoices for current user
     */
    @GetMapping("/my-invoices")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getMyInvoices() {

        List<InvoiceResponse> invoices = invoiceService.getMyInvoices();

        ApiResponse<List<InvoiceResponse>> apiResponse = ApiResponse.<List<InvoiceResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your invoices retrieved successfully")
                .data(invoices)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get unpaid invoices for current user
     */
    @GetMapping("/my-unpaid")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getMyUnpaidInvoices() {

        List<InvoiceResponse> invoices = invoiceService.getMyUnpaidInvoices();

        ApiResponse<List<InvoiceResponse>> apiResponse = ApiResponse.<List<InvoiceResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your unpaid invoices retrieved successfully")
                .data(invoices)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get all invoices
     * Admin only
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getAllInvoices() {

        List<InvoiceResponse> invoices = invoiceService.getAllInvoices();

        ApiResponse<List<InvoiceResponse>> apiResponse = ApiResponse.<List<InvoiceResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All invoices retrieved successfully")
                .data(invoices)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get invoices by status
     * Admin only
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getInvoicesByStatus(
            @PathVariable InvoiceStatus status) {

        List<InvoiceResponse> invoices = invoiceService.getInvoicesByStatus(status);

        ApiResponse<List<InvoiceResponse>> apiResponse = ApiResponse.<List<InvoiceResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Invoices with status " + status + " retrieved successfully")
                .data(invoices)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get overdue invoices
     * Admin only
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getOverdueInvoices() {

        List<InvoiceResponse> invoices = invoiceService.getOverdueInvoices();

        ApiResponse<List<InvoiceResponse>> apiResponse = ApiResponse.<List<InvoiceResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Overdue invoices retrieved successfully")
                .data(invoices)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Get invoices within date range
     * Admin only
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<InvoiceResponse>>> getInvoicesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate) {

        List<InvoiceResponse> invoices = invoiceService.getInvoicesByDateRange(startDate, endDate);

        ApiResponse<List<InvoiceResponse>> apiResponse = ApiResponse.<List<InvoiceResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Invoices retrieved successfully")
                .data(invoices)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    /**
     * Cancel invoice
     * Admin only - can only cancel draft or pending invoices
     */
    @DeleteMapping("/{invoiceId}/cancel")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cancelInvoice(@PathVariable Long invoiceId) {

        invoiceService.cancelInvoice(invoiceId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Invoice cancelled successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}