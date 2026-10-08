package com.digitalhealth.platform.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // ✅ Frontend origins only
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:4200",          // Local dev
                "http://localhost:3000",          // React local dev
                "https://devmatters.in",          // Production
                "https://www.devmatters.in",      // Production www
                "https://*.cloudfront.net"        // CloudFront (testing)
        ));

        // ✅ Required headers for JWT
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "X-XSRF-TOKEN"

        ));

        // ✅ REST methods
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // ✅ JWT / cookies
        config.setAllowCredentials(true);

        // ✅ Cache preflight response
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}