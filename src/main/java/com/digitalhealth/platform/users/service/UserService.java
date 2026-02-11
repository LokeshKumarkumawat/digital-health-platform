package com.digitalhealth.platform.users.service;

import com.digitalhealth.platform.common.enums.AuthProvider;
import com.digitalhealth.platform.common.enums.NotificationType;
import com.digitalhealth.platform.common.exception.BadRequestException;
import com.digitalhealth.platform.common.exception.ResourceNotFoundException;
import com.digitalhealth.platform.common.exception.UnauthorizedException;
import com.digitalhealth.platform.common.security.CustomUserDetails;
import com.digitalhealth.platform.common.security.JwtService;
import com.digitalhealth.platform.common.storage.FileStorageService;
import com.digitalhealth.platform.config.redis.RedisCacheConfig;
import com.digitalhealth.platform.notification.dto.NotificationCreateRequest;
import com.digitalhealth.platform.notification.service.NotificationService;
import com.digitalhealth.platform.users.dto.*;
import com.digitalhealth.platform.users.entity.PasswordResetCode;
import com.digitalhealth.platform.users.entity.User;
import com.digitalhealth.platform.users.mapper.UserMapper;
import com.digitalhealth.platform.users.repository.PasswordResetRepository;
import com.digitalhealth.platform.users.repository.UserRepository;
import com.digitalhealth.platform.role.entity.Role;
import com.digitalhealth.platform.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final NotificationService notificationService;
    private final FileStorageService fileStorageService;

    @Transactional
    @CacheEvict(value = RedisCacheConfig.CacheNames.USER_BY_EMAIL, allEntries = true)
    public UserResponse register(UserRegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        Role defaultRole = roleRepository.findByName("ROLE_PATIENT")
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        List<Role> roles = new ArrayList<>();
        roles.add(defaultRole);

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authProvider(AuthProvider.LOCAL)
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id={} email={}",
                savedUser.getId(), savedUser.getEmail());

        return userMapper.toResponse(savedUser);
    }

    @Transactional
    public LoginResponse login(UserLoginRequest request) {

        log.info("Login attempt for email={}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        // 1️⃣ Check account lock
//        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(OffsetDateTime.now())) {
//            throw new AccountLockedException("Account is temporarily locked. Try later.");
//        }

        try {
            // 2️⃣ Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3️⃣ Reset failed attempts
//            user.setFailedLoginAttempts(0);
//            user.setLockedUntil(null);
//            user.setLastLoginAt(OffsetDateTime.now());

            CustomUserDetails userDetails =
                    (CustomUserDetails) authentication.getPrincipal();

            String token = jwtService.generateToken(userDetails);

            log.info("Login successful for email={}", request.getEmail());

            return LoginResponse.builder()
                    .token(token)
                    .user(userMapper.toResponse(user))
                    .build();

        } catch (BadCredentialsException ex) {

            // 4️⃣ Handle failed attempt
//            int attempts = user.getFailedLoginAttempts() + 1;
//            user.setFailedLoginAttempts(attempts);
//
//            if (attempts >= 5) {
//                user.setLockedUntil(OffsetDateTime.now().plusMinutes(15));
//                log.warn("Account locked for email={}", request.getEmail());
//            }

            throw new BadRequestException("Invalid email or password");
        }
    }


    @Transactional
    public void forgotPassword(String email) {

        userRepository.findByEmail(email).ifPresent(user -> {

            if (user.getAuthProvider() != AuthProvider.LOCAL) {
                return; // OAuth users ignored
            }

            // Invalidate old tokens
            passwordResetRepository.deleteByUserId(user.getId());

            // Generate token
            String rawToken = tokenGenerator.generate();
            String hashedToken = passwordEncoder.encode(rawToken);

            PasswordResetCode token = PasswordResetCode.builder()
                    .user(user)
                    .code(hashedToken)
                    .expiryDate(OffsetDateTime.now().plusMinutes(20))
                    .used(false)
                    .build();

            passwordResetRepository.save(token);

            // 🔗 RESET LINK (IMPORTANT)
            String resetLink =
                    "http://localhost:4200/reset-password?email=" +
                            user.getEmail() + "&code=" + rawToken;


            // Send notification email
            NotificationCreateRequest notificationRequest =
                    NotificationCreateRequest.builder()
                            .userId(user.getId())
                            .type(NotificationType.EMAIL)
                            .recipient(user.getEmail())
                            .subject("Password Reset Request")
                            .templateName("password-reset")
                            .templateVariables(Map.of(
                                    "name", user.getName(),
                                    "resetLink", resetLink,
                                    "expiryMinutes", "20"
                            ))
                            .message("Password reset code") // fallback
                            .build();

            notificationService.sendEmail(notificationRequest, user);
            log.info("Password reset email queued for userId={}", user.getId());
        });
        log.info("Password reset code sent to token: {}", email);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CacheNames.USERS, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.CacheNames.USER_BY_EMAIL, key = "#request.email")
    })
    public void resetPassword(UserResetPasswordRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        PasswordResetCode resetToken = passwordResetRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (resetToken.isUsed()
                || resetToken.getExpiryDate().isBefore(OffsetDateTime.now())
                || !passwordEncoder.matches(request.getResetCode(), resetToken.getCode())) {
            throw new BadRequestException("Invalid token");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        resetToken.setUsed(true);

        passwordResetRepository.deleteByUserId(user.getId()); // cleanup
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CacheNames.USERS, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.CacheNames.USER_BY_EMAIL, allEntries = true)
    })
    public void changePassword(UserChangePasswordRequest request) {
        log.info("Password change requested");

        User user = getCurrentAuthenticatedUser();

        if (user.getAuthProvider() != AuthProvider.LOCAL) {
            throw new IllegalStateException("Password change not available for OAuth2 users");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user: {}", user.getEmail());
    }


    @Transactional
    public LoginResponse loginRegisterByGoogleOAuth2(OAuth2AuthenticationToken authenticationToken) {

        OAuth2User oauth2User = authenticationToken.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        User user = userRepository.findByEmail(email)
                .map(existingUser -> {

                    // 🚨 Prevent provider hijacking
                    if (existingUser.getAuthProvider() != AuthProvider.GOOGLE) {
                        throw new BadRequestException(
                                "Account already exists with different authentication method");
                    }

                    return existingUser;
                })
                .orElseGet(() -> {

                    Role defaultRole = roleRepository.findByName("ROLE_PATIENT")
                            .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

                    return userRepository.save(
                            User.builder()
                                    .name(name)
                                    .email(email)
                                    .authProvider(AuthProvider.GOOGLE)
                                    .profilePictureUrl(picture)
                                    .roles(List.of(defaultRole))
//                                    .tokenVersion(0) // IMPORTANT
                                    .build()
                    );
                });


        CustomUserDetails userDetails = new CustomUserDetails(user);
        // ✅ Always include token version
        String token = jwtService.generateToken(userDetails);

        return LoginResponse.builder()
                .token(token)
                .user(userMapper.toResponse(user))
                .build();
    }


    public UserResponse getCurrentUser() {
        log.debug("Fetching current authenticated user");
        User user = getCurrentAuthenticatedUser();
        return userMapper.toResponse(user);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CacheNames.USERS, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.CacheNames.USER_BY_EMAIL, allEntries = true)
    })
    public UserResponse updateCurrentUser(UserUpdateRequest request) {

        log.info("Updating current user profile");

        User user = getCurrentAuthenticatedUser();

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getProfilePictureUrl() != null && !request.getProfilePictureUrl().isBlank()) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        User updatedUser = userRepository.save(user);

        log.info("User profile updated successfully for user id={}", updatedUser.getId());

        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CacheNames.USERS, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.CacheNames.USER_BY_EMAIL, allEntries = true)
    })
    public String uploadProfilePicture(MultipartFile file) {

        log.info("Uploading profile picture");

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Profile picture file is required");
        }

        User user = getCurrentAuthenticatedUser();

        String profilePictureUrl = fileStorageService.storeFile(file);

        user.setProfilePictureUrl(profilePictureUrl);
        userRepository.save(user);

        log.info("Profile picture uploaded successfully for user={}", user.getEmail());

        return profilePictureUrl;
    }


    public UserResponse getUserById(Long userId) {

        log.debug("Fetching user with id={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId));

        return userMapper.toResponse(user);
    }


    @Cacheable(
            value = RedisCacheConfig.CacheNames.USERS,
            key = "#userId",
            unless = "#result == null"
    )
    public List<UserResponse> getAllUsers() {

        log.debug("Fetching all users");

        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }


    // Get all users - Not cached (list operations typically not cached)
    public List<UserSummaryResponse> getAllUsersSummary() {

        log.debug("Fetching all users summary");

        return userRepository.findAll().stream()
                .map(userMapper::toSummary)
                .collect(Collectors.toList());
    }


    @Transactional
    @Caching(evict = {
            @CacheEvict(value = RedisCacheConfig.CacheNames.USERS, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.CacheNames.USER_BY_EMAIL, allEntries = true),
            @CacheEvict(value = RedisCacheConfig.CacheNames.USER_ROLES, allEntries = true)
    })
    public void deleteUser(Long userId) {

        log.info("Deleting user with id={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found with id: " + userId));

        User currentUser = getCurrentAuthenticatedUser();

        if (currentUser.getId().equals(userId)) {
            throw new BadRequestException("You cannot delete your own account");
        }

        passwordResetRepository.deleteByUserId(userId);
        userRepository.delete(user);

        log.info("User deleted successfully with id={}", userId);
    }


    private User getCurrentAuthenticatedUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException("User not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found"));
    }
}