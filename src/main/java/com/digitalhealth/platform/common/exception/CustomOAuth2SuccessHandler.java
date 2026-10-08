package com.digitalhealth.platform.common.exception;

import com.digitalhealth.platform.users.dto.LoginResponse;
import com.digitalhealth.platform.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    @Value("${app.frontend.redirect-url}")
    private String frontendRedirectUrl;

    public CustomOAuth2SuccessHandler(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        if (!(authentication instanceof OAuth2AuthenticationToken oAuth2Token)) {
            // ✅ FIXED: Redirect to home (sidebar will open on home)
            response.sendRedirect(frontendRedirectUrl + "/?oauth_error=invalid_token");
            return;
        }

        try {
            // Google डेटा से यूज़र को रजिस्टर/लॉगिन करें
            LoginResponse loginResponse = userService.loginRegisterByGoogleOAuth2(oAuth2Token);
            String jwtToken = loginResponse.getToken();

            // ✅ Use Spring's ResponseCookie to add SameSite attribute
            ResponseCookie jwtCookie = ResponseCookie.from("ACCESS_TOKEN", jwtToken)
                    .httpOnly(true)
                    .secure(true)       // Localhost पर भी ब्राउज़र इसे allow करते हैं
                    .path("/")
                    .maxAge(60 * 60)    // 1 hour
                    .sameSite("None")   // Cross-Origin (4200 to 8080) के लिए 'None' बहुत ज़रूरी है
                    .build();

            // कुकी को हेडर में सेट करें
            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            // ✅ FIXED: Redirect to OAuth callback page
            response.sendRedirect(frontendRedirectUrl + "/oauth/callback?status=success");

        } catch (Exception e) {
            log.error("Google OAuth2 Login failed", e);
            // ✅ FIXED: Redirect to OAuth callback with error
            response.sendRedirect(frontendRedirectUrl + "/oauth/callback?status=error&message=" + e.getMessage());
        }
    }
}