package com.digitalhealth.platform.config.ratelimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Rate Limiting Configuration using Bucket4j and Redis
 *
 * Implements token bucket algorithm for distributed rate limiting
 */
@Configuration
@Slf4j
public class RateLimitConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    /**
     * Create Redis client for Bucket4j
     */
    @Bean
    public RedisClient redisClient() {
        String redisUri = String.format("redis://%s:%d", redisHost, redisPort);
        log.info("Creating Redis client for rate limiting: {}", redisUri);
        return RedisClient.create(redisUri);
    }

    /**
     * Create Redis connection for Bucket4j
     */
    @Bean
    public StatefulRedisConnection<String, byte[]> redisConnection(RedisClient redisClient) {
        return redisClient.connect(io.lettuce.core.codec.RedisCodec.of(
                io.lettuce.core.codec.StringCodec.UTF8,
                io.lettuce.core.codec.ByteArrayCodec.INSTANCE
        ));
    }

    /**
     * Create Bucket4j ProxyManager for distributed rate limiting
     */
    @Bean
    public ProxyManager<String> proxyManager(StatefulRedisConnection<String, byte[]> connection) {
        return LettuceBasedProxyManager.builderFor(connection)
                .build();
    }

    /**
     * Rate limit configurations
     */
    public static class RateLimits {

        // API Rate Limits (per minute)
        public static final int PUBLIC_API_LIMIT = 20;           // 20 requests/min for unauthenticated
        public static final int AUTHENTICATED_API_LIMIT = 100;   // 100 requests/min for authenticated users
        public static final int ADMIN_API_LIMIT = 200;           // 200 requests/min for admins

        // Specific endpoint limits (per minute)
        public static final int LOGIN_LIMIT = 5;                 // 5 login attempts/min
        public static final int REGISTER_LIMIT = 3;              // 3 registration attempts/min
        public static final int PASSWORD_RESET_LIMIT = 3;        // 3 password reset/min
        public static final int PAYMENT_LIMIT = 10;              // 10 payment operations/min
        public static final int APPOINTMENT_BOOKING_LIMIT = 5;   // 5 bookings/min

        // Email/SMS limits (per hour)
        public static final int EMAIL_LIMIT = 10;                // 10 emails/hour
        public static final int SMS_LIMIT = 5;                   // 5 SMS/hour

        // Search limits (per minute)
        public static final int SEARCH_LIMIT = 30;               // 30 searches/min

        // Download limits (per hour)
        public static final int DOWNLOAD_LIMIT = 50;             // 50 downloads/hour
    }

    /**
     * Create bucket configuration for general API access
     */
    public static Supplier<BucketConfiguration> publicApiConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RateLimits.PUBLIC_API_LIMIT)
                        .refillIntervally(RateLimits.PUBLIC_API_LIMIT, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    /**
     * Create bucket configuration for authenticated users
     */
    public static Supplier<BucketConfiguration> authenticatedApiConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RateLimits.AUTHENTICATED_API_LIMIT)
                        .refillIntervally(RateLimits.AUTHENTICATED_API_LIMIT, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    /**
     * Create bucket configuration for admin users
     */
    public static Supplier<BucketConfiguration> adminApiConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RateLimits.ADMIN_API_LIMIT)
                        .refillIntervally(RateLimits.ADMIN_API_LIMIT, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    /**
     * Create bucket configuration for login attempts
     */
    public static Supplier<BucketConfiguration> loginConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RateLimits.LOGIN_LIMIT)
                        .refillIntervally(RateLimits.LOGIN_LIMIT, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    /**
     * Create bucket configuration for registration
     */
    public static Supplier<BucketConfiguration> registerConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RateLimits.REGISTER_LIMIT)
                        .refillIntervally(RateLimits.REGISTER_LIMIT, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    /**
     * Create bucket configuration for password reset
     */
    public static Supplier<BucketConfiguration> passwordResetConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RateLimits.PASSWORD_RESET_LIMIT)
                        .refillIntervally(RateLimits.PASSWORD_RESET_LIMIT, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    /**
     * Create bucket configuration for payment operations
     */
    public static Supplier<BucketConfiguration> paymentConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RateLimits.PAYMENT_LIMIT)
                        .refillIntervally(RateLimits.PAYMENT_LIMIT, Duration.ofMinutes(1))
                        .build())
                .build();
    }


    /**
     * Create bucket configuration for appointment booking
     */
    public static Supplier<BucketConfiguration> appointmentBookingConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RateLimits.APPOINTMENT_BOOKING_LIMIT)
                        .refillIntervally(RateLimits.APPOINTMENT_BOOKING_LIMIT, Duration.ofMinutes(1))
                        .build())
                .build();
    }

    /**
     * Create bucket configuration for email sending
     */
    public static Supplier<BucketConfiguration> emailConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RateLimits.EMAIL_LIMIT)
                        .refillIntervally(RateLimits.EMAIL_LIMIT, Duration.ofHours(1))
                        .build())
                .build();
    }

    /**
     * Create bucket configuration for search operations
     */
    public static Supplier<BucketConfiguration> searchConfig() {
        return () -> BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(RateLimits.SEARCH_LIMIT)
                        .refillIntervally(RateLimits.SEARCH_LIMIT, Duration.ofMinutes(1))
                        .build())
                .build();
    }

}