package com.digitalhealth.platform.ratelimit.interceptor;


import com.digitalhealth.platform.ratelimit.RateLimit;
import com.digitalhealth.platform.ratelimit.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor to handle rate limiting for annotated endpoints
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);

        if (rateLimit == null) {
            return true; // No rate limiting for this endpoint
        }

        // Get identifier (IP or userId)
        String identifier = getIdentifier(request);

        // Check rate limit based on type
        boolean allowed = checkRateLimit(identifier, rateLimit.type());

        if (!allowed) {
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "statusCode": 429,
                    "error": "Too Many Requests",
                    "message": "Rate limit exceeded. Please try again later."
                }
                """);

            log.warn("Rate limit exceeded for {} on endpoint: {}",
                    identifier, request.getRequestURI());
            return false;
        }

        return true;
    }

    /**
     * Get identifier for rate limiting
     * Uses userId if authenticated, otherwise uses IP address
     */
    private String getIdentifier(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName(); // Email or username
        }

        // Fall back to IP address for unauthenticated requests
        return getClientIp(request);
    }

    /**
     * Get client IP address (handles proxy headers)
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Handle multiple IPs (take first one)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip != null ? ip : "unknown";
    }

    /**
     * Check rate limit based on type
     */
    private boolean checkRateLimit(String identifier, RateLimit.Type type) {
        return switch (type) {
            case PUBLIC_API -> rateLimitService.isPublicApiAllowed(identifier);
            case AUTHENTICATED_API -> rateLimitService.isAuthenticatedApiAllowed(identifier);
            case ADMIN_API -> rateLimitService.isAdminApiAllowed(identifier);
            case LOGIN -> rateLimitService.isLoginAllowed(identifier);
            case REGISTER -> rateLimitService.isRegistrationAllowed(identifier);
            case PASSWORD_RESET -> rateLimitService.isPasswordResetAllowed(identifier);
            case PAYMENT -> rateLimitService.isPaymentAllowed(identifier);
            case APPOINTMENT_BOOKING -> rateLimitService.isAppointmentBookingAllowed(identifier);
            case EMAIL -> rateLimitService.isEmailAllowed(identifier);
            case SEARCH -> rateLimitService.isSearchAllowed(identifier);
        };
    }
}