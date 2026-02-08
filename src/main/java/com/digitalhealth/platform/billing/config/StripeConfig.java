package com.digitalhealth.platform.billing.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Stripe configuration
 * Initializes Stripe with API key
 */
@Configuration
@Slf4j
public class StripeConfig {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        log.info("Stripe API initialized");

        if (webhookSecret != null && !webhookSecret.isEmpty()) {
            log.info("Stripe webhook secret configured");
        } else {
            log.warn("Stripe webhook secret not configured");
        }
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }
}