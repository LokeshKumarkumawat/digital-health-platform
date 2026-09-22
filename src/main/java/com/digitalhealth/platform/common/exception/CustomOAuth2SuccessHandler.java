package com.digitalhealth.platform.common.exception;

import com.digitalhealth.platform.users.dto.LoginResponse;
import com.digitalhealth.platform.users.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


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
                                        Authentication authentication)
            throws IOException {

        if (!(authentication instanceof OAuth2AuthenticationToken oAuth2Token)) {
            response.sendRedirect(frontendRedirectUrl + "/register");
            return;
        }

        LoginResponse loginResponse =
                userService.loginRegisterByGoogleOAuth2(oAuth2Token);

        String jwtToken = loginResponse.getToken();

        // ✅ Store JWT in secure HTTP-only cookie
        Cookie jwtCookie = new Cookie("ACCESS_TOKEN", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true); // true in HTTPS
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60); // 1 hour

        response.addCookie(jwtCookie);

        // ✅ Redirect WITHOUT token
        response.sendRedirect(frontendRedirectUrl + "/home");
    }
}