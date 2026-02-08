package com.digitalhealth.platform.billing.controller;

import com.digitalhealth.platform.billing.config.StripeConfig;
import com.digitalhealth.platform.billing.payment.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Webhook controller for handling Stripe events
 *
 * Important: This endpoint should NOT have authentication
 * Stripe will send events to this endpoint
 *
 * Webhook URL (for Stripe dashboard):
 * https://your-domain.com/api/v1/billing/webhook
 */
@RestController
@RequestMapping("/api/v1/billing")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final PaymentService paymentService;
    private final StripeConfig stripeConfig;

    /**
     * Handle Stripe webhook events
     *
     * Events we handle:
     * - payment_intent.succeeded
     * - payment_intent.payment_failed
     * - payment_intent.canceled
     * - charge.refunded
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            // Verify webhook signature
            event = Webhook.constructEvent(
                    payload,
                    sigHeader,
                    stripeConfig.getWebhookSecret()
            );
        } catch (SignatureVerificationException e) {
            log.error("Invalid webhook signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;
            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(event);
                break;
            case "payment_intent.canceled":
                handlePaymentIntentCanceled(event);
                break;
            case "charge.refunded":
                handleChargeRefunded(event);
                break;
            default:
                log.info("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Webhook handled");
    }

    /**
     * Handle successful payment
     */
    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (paymentIntent != null) {
            log.info("Payment succeeded: {}", paymentIntent.getId());
            try {
                paymentService.confirmPayment(paymentIntent.getId());
            } catch (StripeException e) {
                log.error("Error confirming payment: {}", e.getMessage());
            }
        }
    }

    /**
     * Handle failed payment
     */
    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (paymentIntent != null) {
            log.warn("Payment failed: {}", paymentIntent.getId());
            try {
                paymentService.confirmPayment(paymentIntent.getId());
            } catch (StripeException e) {
                log.error("Error updating failed payment: {}", e.getMessage());
            }
        }
    }

    /**
     * Handle canceled payment
     */
    private void handlePaymentIntentCanceled(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElse(null);

        if (paymentIntent != null) {
            log.info("Payment canceled: {}", paymentIntent.getId());
            try {
                paymentService.confirmPayment(paymentIntent.getId());
            } catch (StripeException e) {
                log.error("Error updating canceled payment: {}", e.getMessage());
            }
        }
    }

    /**
     * Handle refunded charge
     */
    private void handleChargeRefunded(Event event) {
        log.info("Charge refunded event received");
        // Additional refund handling can be added here
    }
}