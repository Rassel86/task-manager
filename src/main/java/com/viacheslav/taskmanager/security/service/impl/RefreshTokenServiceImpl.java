package com.viacheslav.taskmanager.security.service.impl;

import com.viacheslav.taskmanager.model.UserAccount;
import com.viacheslav.taskmanager.security.repository.RefreshTokenRepository;
import com.viacheslav.taskmanager.security.model.RefreshToken;
import com.viacheslav.taskmanager.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public String generateRefreshToken(UserAccount user) {
        byte[] randomBytes = new byte[32];
        SECURE_RANDOM.nextBytes(randomBytes);
        String rawToken = Base64.getEncoder().withoutPadding().encodeToString(randomBytes);

        String tokenHash = DigestUtils.sha256Hex(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(refreshToken);

        return rawToken;
    }

    @Transactional(readOnly = true)
    public boolean isRefreshTokenValid(String rawToken) {
        RefreshToken refreshToken = getRefreshToken(rawToken);

        if (refreshToken == null) {
            log.info("RT is null");
            return false;
        }

        if (refreshToken.isRevoked()) {
            log.info("RT is revoked");
            return false;
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.info("RT is expired");
            return false;
        }

        return true;
    }

    @Override
    public void revokeRefreshToken(String rawToken) {
        RefreshToken refreshToken = getRefreshToken(rawToken);
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public UserAccount getUserByRefreshToken(String rawToken) {
        RefreshToken refreshToken = getRefreshToken(rawToken);
        return refreshToken.getUser();
    }

    private RefreshToken getRefreshToken(String rawToken) {
        String tokenHash = DigestUtils.sha256Hex(rawToken);
        return refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }
}
