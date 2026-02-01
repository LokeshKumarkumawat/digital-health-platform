package com.digitalhealth.platform.common.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String token = extractToken(request);

            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                String username = jwtService.extractUsername(token);

                if (StringUtils.hasText(username)) {

                    UserDetails userDetails =
                            userDetailsService.loadUserByUsername(username);

                    if (!jwtService.isTokenValid(token, userDetails)) {
                        throw new JwtException("Invalid JWT");
                    }

                    if (jwtService.isTokenValid(token, userDetails)) {

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource()
                                        .buildDetails(request)
                        );

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            log.error("JWT authentication failed, traceId={}", MDC.get("traceId"));
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response); // let Spring Security handle 401
        }
    }

    private String extractToken(HttpServletRequest request) {
        // 1️⃣ Try Authorization header (API / Mobile)
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}