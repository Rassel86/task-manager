package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.dto.auth.AuthResponse;
import com.viacheslav.taskmanager.dto.auth.LoginRequest;
import com.viacheslav.taskmanager.dto.auth.RegisterRequest;
import com.viacheslav.taskmanager.dto.user.UserCreateRequest;
import com.viacheslav.taskmanager.dto.user.UserResponse;
import com.viacheslav.taskmanager.entity.User;
import com.viacheslav.taskmanager.exception.InvalidTokenException;
import com.viacheslav.taskmanager.exception.PasswordsDontMatchException;
import com.viacheslav.taskmanager.mapper.AuthMapper;
import com.viacheslav.taskmanager.security.model.CurrentUser;
import com.viacheslav.taskmanager.security.service.JwtService;
import com.viacheslav.taskmanager.service.AuthService;
import com.viacheslav.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        log.info("Processing registration for {}", request.email());
        validateRegistration(request);
        String encodedPassword = passwordEncoder.encode(request.password());
        UserCreateRequest createRequest = authMapper.toUserCreateRequest(request, encodedPassword);
        return userService.createRegisteredUser(createRequest);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usernameOrEmail(), request.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.findByUsernameOrEmail(request.usernameOrEmail());
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(authentication);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
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
        log.info("Refreshing token");

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        User user = userService.findByUsernameOrEmail(username);
        UserDetails userDetails = new CurrentUser(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        String newAccessToken = jwtService.generateAccessToken(authentication);
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
            throw new PasswordsDontMatchException("Password and confirmation password mismatch");
        }
    }
}
