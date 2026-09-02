package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.exception.AccountDisabledException;
import com.viacheslav.taskmanager.exception.InvalidTokenException;
import com.viacheslav.taskmanager.exception.PasswordsDontMatchException;
import com.viacheslav.taskmanager.model.UserAccount;
import com.viacheslav.taskmanager.model.dto.auth.AuthResult;
import com.viacheslav.taskmanager.model.dto.auth.LoginRequest;
import com.viacheslav.taskmanager.model.dto.auth.RefreshTokenResponse;
import com.viacheslav.taskmanager.model.dto.auth.RegisterRequest;
import com.viacheslav.taskmanager.model.dto.user.UserCreateDto;
import com.viacheslav.taskmanager.model.dto.user.UserResponse;
import com.viacheslav.taskmanager.security.model.CustomUserDetails;
import com.viacheslav.taskmanager.security.service.CustomUserDetailsService;
import com.viacheslav.taskmanager.security.service.JwtService;
import com.viacheslav.taskmanager.security.service.RefreshTokenService;
import com.viacheslav.taskmanager.service.AuthService;
import com.viacheslav.taskmanager.service.UserAccountService;
import com.viacheslav.taskmanager.util.LoggingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserAccountService userAccountService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Processing registration for user with email {} and username {}",
                LoggingUtils.maskEmail(request.email()), LoggingUtils.maskUsername(request.displayName()));

        if (!request.password().equals(request.confirmPassword())) {
            log.warn("Registration failed - passwords mismatch for email: {}", LoggingUtils.maskEmail(request.email()));
            throw new PasswordsDontMatchException("Password and confirmation password mismatch");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        UserCreateDto createRequest = UserCreateDto.builder()
                .displayName(request.displayName())
                .contactEmail(request.email())
                .password(encodedPassword)
                .build();

        UserResponse response = userAccountService.createUserAccount(createRequest);
        log.info("Registration successful for user with email {}", LoggingUtils.maskEmail(response.contactEmail()));
        return response;
    }

    @Override
    @Transactional
    public AuthResult login(LoginRequest request) {
        log.info("Login attempt for {}", request.login());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.login(), request.password()));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UserAccount userAccount = customUserDetails.getUserAccount();

        validateUserStatus(userAccount);

        String accessToken = jwtService.generateAccessToken(userAccount);
        String refreshToken = refreshTokenService.generateRefreshToken(userAccount);

        log.info("Login successful - email: {}, username: {}",
                LoggingUtils.maskEmail(userAccount.getContactEmail()), LoggingUtils.maskUsername(userAccount.getDisplayName()));
        return AuthResult.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshAccessToken(String rawRefreshToken) {
        log.info("Token refreshing attempt");

        if (!refreshTokenService.isRefreshTokenValid(rawRefreshToken)) {
            log.warn("Token refresh failed - invalid or expired token");
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        UserAccount userAccount = refreshTokenService.getUserByRefreshToken(rawRefreshToken);
        validateUserStatus(userAccount);

        refreshTokenService.revokeRefreshToken(rawRefreshToken);

        String newAccessToken = jwtService.generateAccessToken(userAccount);
        String newRefreshToken = refreshTokenService.generateRefreshToken(userAccount);

        log.info("Token refresh successful for user account with email {}", LoggingUtils.maskEmail(userAccount.getContactEmail()));
        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private void validateUserStatus(UserAccount userAccount) {
        if (!userAccount.isEnabled()) {
            log.warn("UserAccount {} disabled", userAccount.getId());
            throw new AccountDisabledException("UserAccount account is disabled");
        }
    }
}
