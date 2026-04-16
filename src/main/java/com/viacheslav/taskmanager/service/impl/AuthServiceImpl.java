package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.exception.AccountDisabledException;
import com.viacheslav.taskmanager.exception.InvalidTokenException;
import com.viacheslav.taskmanager.exception.PasswordsDontMatchException;
import com.viacheslav.taskmanager.mapper.AuthMapper;
import com.viacheslav.taskmanager.model.User;
import com.viacheslav.taskmanager.model.dto.auth.AuthResponse;
import com.viacheslav.taskmanager.model.dto.auth.LoginRequest;
import com.viacheslav.taskmanager.model.dto.auth.RegisterRequest;
import com.viacheslav.taskmanager.model.dto.user.UserCreateRequest;
import com.viacheslav.taskmanager.model.dto.user.UserResponse;
import com.viacheslav.taskmanager.security.model.CurrentUser;
import com.viacheslav.taskmanager.security.service.CustomUserDetailsService;
import com.viacheslav.taskmanager.security.service.JwtService;
import com.viacheslav.taskmanager.service.AuthService;
import com.viacheslav.taskmanager.service.UserService;
import com.viacheslav.taskmanager.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Processing registration for user with email {} and username {}",
                LoggingUtils.maskEmail(request.email()), LoggingUtils.maskUsername(request.username()));

        validateRegistration(request);

        String encodedPassword = passwordEncoder.encode(request.password());

        UserCreateRequest createRequest = authMapper.toUserCreateRequest(request, encodedPassword);
        UserResponse response = userService.createRegisteredUser(createRequest);
        log.info("Registration successful for user with email {}", LoggingUtils.maskEmail(response.email()));
        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for {}", request.usernameOrEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));

        CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
        User user = currentUser.getUser();

        validateUserStatus(user);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(currentUser);

        log.info("Login successful - email: {}, username: {}",
                LoggingUtils.maskEmail(user.getEmail()), LoggingUtils.maskUsername(user.getUsername()));
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getAuthority())
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Token refreshing attempt");

        if (!jwtService.isTokenValid(refreshToken)) {
            log.warn("Token refresh failed - invalid or expired token");
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        CurrentUser currentUser = userDetailsService.loadUserByUsername(username);

        User user = currentUser.getUser();

        validateUserStatus(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                currentUser, null, currentUser.getAuthorities());
        String newAccessToken = jwtService.generateAccessToken(authentication);
        log.info("Token refresh successful for user with email {}", LoggingUtils.maskEmail(user.getEmail()));
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().getAuthority())
                .build();
    }

    private void validateRegistration(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            log.warn("Registration failed - passwords mismatch for email: {}", LoggingUtils.maskEmail(request.email()));
            throw new PasswordsDontMatchException("Password and confirmation password mismatch");
        }
    }

    private void validateUserStatus(User user) {
        if (!user.isEnabled()) {
            log.warn("User {} disabled", user.getId());
            throw new AccountDisabledException("User account is disabled");
        }
    }
}
