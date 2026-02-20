package com.digitalhealth.platform.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to apply rate limiting to controller methods
 *
 * Usage:
 * @RateLimit(type = RateLimit.Type.LOGIN)
 * public ResponseEntity<?> login() { }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * Type of rate limit to apply
     */
    Type type() default Type.AUTHENTICATED_API;

    /**
     * Rate limit types
     */
    enum Type {
        PUBLIC_API,
        AUTHENTICATED_API,
        ADMIN_API,
        LOGIN,
        REGISTER,
        PASSWORD_RESET,
        PAYMENT,
        APPOINTMENT_BOOKING,
        EMAIL,
        SEARCH
    }
}