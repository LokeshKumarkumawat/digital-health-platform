package com.digitalhealth.platform.users.controller;


import com.digitalhealth.platform.common.response.ApiResponse;
import com.digitalhealth.platform.ratelimit.RateLimit;
import com.digitalhealth.platform.users.dto.*;
import com.digitalhealth.platform.users.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegisterRequest request) {
        UserResponse response = userService.register(request);

        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("User registered successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/login")
    @RateLimit(type = RateLimit.Type.LOGIN)
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody UserLoginRequest request) {

        // 1. सर्विस से यूजर का डेटा और टोकन लें
        LoginResponse response = userService.login(request);

        // 2. Spring का ResponseCookie इस्तेमाल करें (ताकि SameSite=None सेट हो सके)
        ResponseCookie jwtCookie = ResponseCookie.from("ACCESS_TOKEN", response.getToken())
                .httpOnly(true)
                .secure(true)       // HTTPS या Localhost के लिए ज़रूरी
                .path("/")
                .maxAge(60 * 60)    // 1 घंटे के लिए (सेकंड्स में)
                .sameSite("None")   // Angular (4200) से Backend (8080) के लिए अनिवार्य
                .build();

        response.setToken(null);

        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .data(response)     // आप चाहें तो सुरक्षा के लिए यहाँ से टोकन हटा सकते हैं, क्योंकि वह अब कुकी में है
                .traceId(MDC.get("traceId"))
                .build();

        // 3. कुकी को हेडर में रखकर रिस्पॉन्स भेजें
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(apiResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        userService.forgotPassword(request.getEmail());

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("If the email exists, password reset instructions have been sent")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @Valid @RequestBody UserResetPasswordRequest request) {

        userService.resetPassword(request);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password reset successful")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }




    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {

        UserResponse response = userService.getCurrentUser();

        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Current user details retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentUser(
            @Valid @RequestBody UserUpdateRequest request) {

        UserResponse response = userService.updateCurrentUser(request);

        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User profile updated successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/me/profile-picture")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file) {

        String profilePictureUrl = userService.uploadProfilePicture(file);

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Profile picture uploaded successfully")
                .data(profilePictureUrl)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {

        UserResponse response = userService.getUserById(userId);

        ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User details retrieved successfully")
                .data(response)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        ApiResponse<List<UserResponse>> apiResponse = ApiResponse.<List<UserResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All users retrieved successfully")
                .data(users)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DOCTOR')")
    public ResponseEntity<ApiResponse<List<UserSummaryResponse>>> getAllUsersSummary() {

        List<UserSummaryResponse> users = userService.getAllUsersSummary();

        ApiResponse<List<UserSummaryResponse>> apiResponse = ApiResponse.<List<UserSummaryResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User summaries retrieved successfully")
                .data(users)
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {

        userService.deleteUser(userId);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User deleted successfully")
                .traceId(MDC.get("traceId"))
                .build();

        return ResponseEntity.ok(apiResponse);
    }

//    @PostMapping("/oauth2/google")
//    public ResponseEntity<ApiResponse<LoginResponse>> loginWithGoogle(OAuth2AuthenticationToken authenticationToken) {
//        LoginResponse response = userService.loginRegisterByGoogleOAuth2(authenticationToken);
//
//        ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
//                .statusCode(HttpStatus.OK.value())
//                .message("Google OAuth2 login successful")
//                .data(response)
//                .traceId(UUID.randomUUID().toString())
//                .build();
//
//        return ResponseEntity.ok(apiResponse);
//    }

    @GetMapping("/oauth2/google")
    public ResponseEntity<String> googleLogin() {
        return ResponseEntity.ok("/oauth2/authorization/google");
    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {

        // खाली वैल्यू और 0 MaxAge के साथ कुकी को ओवरराइट करें
        ResponseCookie deleteCookie = ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)          // 0 मतलब ब्राउज़र इस कुकी को तुरंत डिलीट कर देगा
                .sameSite("None")   // यह मैच होना ज़रूरी है
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }


}