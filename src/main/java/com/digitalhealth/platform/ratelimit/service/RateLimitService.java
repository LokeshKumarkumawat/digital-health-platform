package com.digitalhealth.platform.ratelimit.service;

import com.digitalhealth.platform.config.ratelimiter.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Service for rate limiting operations
 * Uses token bucket algorithm with Redis for distributed rate limiting
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final ProxyManager<String> proxyManager;

    /**
     * Check if request is allowed for given key and configuration
     *
     * @param key Unique identifier (IP, userId, email, etc.)
     * @param configSupplier Bucket configuration supplier
     * @return true if allowed, false if rate limited
     */
    public boolean isAllowed(String key, Supplier<BucketConfiguration> configSupplier) {
        Bucket bucket = proxyManager.builder().build(key, configSupplier);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            log.debug("Request allowed for key: {}. Remaining tokens: {}", key, probe.getRemainingTokens());
            return true;
        } else {
            log.warn("Rate limit exceeded for key: {}. Retry after: {} seconds",
                    key, probe.getNanosToWaitForRefill() / 1_000_000_000);
            return false;
        }
    }

    /**
     * Get remaining tokens for a key
     */
    public long getRemainingTokens(String key, Supplier<BucketConfiguration> configSupplier) {
        Bucket bucket = proxyManager.builder().build(key, configSupplier);
        return bucket.getAvailableTokens();
    }

    /**
     * Get time until next refill in seconds
     */
    public long getSecondsUntilRefill(String key, Supplier<BucketConfiguration> configSupplier) {
        Bucket bucket = proxyManager.builder().build(key, configSupplier);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (!probe.isConsumed()) {
            return probe.getNanosToWaitForRefill() / 1_000_000_000;
        }
        return 0;
    }

    /**
     * Reset rate limit for a key
     */
    public void resetRateLimit(String key) {
        // Note: Bucket4j doesn't have built-in reset, so we need to use ProxyManager
        // This will remove the key from Redis, effectively resetting it
        log.info("Resetting rate limit for key: {}", key);
        // Implementation depends on your use case
        // You might want to delete the Redis key manually
    }

    // ==================== CONVENIENCE METHODS ====================

    /**
     * Check public API rate limit (20 req/min)
     */
    public boolean isPublicApiAllowed(String identifier) {
        String key = "public_api:" + identifier;
        return isAllowed(key, RateLimitConfig.publicApiConfig());
    }

    /**
     * Check authenticated API rate limit (100 req/min)
     */
    public boolean isAuthenticatedApiAllowed(String userId) {
        String key = "auth_api:" + userId;
        return isAllowed(key, RateLimitConfig.authenticatedApiConfig());
    }

    /**
     * Check admin API rate limit (200 req/min)
     */
    public boolean isAdminApiAllowed(String userId) {
        String key = "admin_api:" + userId;
        return isAllowed(key, RateLimitConfig.adminApiConfig());
    }

    /**
     * Check login rate limit (5 attempts/min)
     */
    public boolean isLoginAllowed(String identifier) {
        String key = "login:" + identifier;
        return isAllowed(key, RateLimitConfig.loginConfig());
    }

    /**
     * Check registration rate limit (3 attempts/min)
     */
    public boolean isRegistrationAllowed(String identifier) {
        String key = "register:" + identifier;
        return isAllowed(key, RateLimitConfig.registerConfig());
    }

    /**
     * Check password reset rate limit (3 attempts/min)
     */
    public boolean isPasswordResetAllowed(String identifier) {
        String key = "password_reset:" + identifier;
        return isAllowed(key, RateLimitConfig.passwordResetConfig());
    }

    /**
     * Check payment rate limit (10 operations/min)
     */
    public boolean isPaymentAllowed(String userId) {
        String key = "payment:" + userId;
        return isAllowed(key, RateLimitConfig.paymentConfig());
    }

    /**
     * Check appointment booking rate limit (5 bookings/min)
     */
    public boolean isAppointmentBookingAllowed(String userId) {
        String key = "appointment_booking:" + userId;
        return isAllowed(key, RateLimitConfig.appointmentBookingConfig());
    }

    /**
     * Check email sending rate limit (10 emails/hour)
     */
    public boolean isEmailAllowed(String userId) {
        String key = "email:" + userId;
        return isAllowed(key, RateLimitConfig.emailConfig());
    }

    /**
     * Check search rate limit (30 searches/min)
     */
    public boolean isSearchAllowed(String userId) {
        String key = "search:" + userId;
        return isAllowed(key, RateLimitConfig.searchConfig());
    }

    /**
     * Get rate limit info for a key
     */
    public RateLimitInfo getRateLimitInfo(String key, Supplier<BucketConfiguration> configSupplier) {
        Bucket bucket = proxyManager.builder().build(key, configSupplier);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(0); // Don't consume

        return RateLimitInfo.builder()
                .remainingTokens(bucket.getAvailableTokens())
                .isAllowed(probe.getRemainingTokens() > 0)
                .secondsUntilRefill(probe.getNanosToWaitForRefill() / 1_000_000_000)
                .build();
    }

    /**
     * Rate limit information DTO
     */
    @lombok.Data
    @lombok.Builder
    public static class RateLimitInfo {
        private long remainingTokens;
        private boolean isAllowed;
        private long secondsUntilRefill;
    }
}