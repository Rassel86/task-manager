package com.viacheslav.taskmanager.security.service.impl;

import com.viacheslav.taskmanager.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 17 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Start delete expired refresh tokens");
        int deleted = refreshTokenRepository.deleteAllByExpiresAtBeforeOrRevokedIsTrue(LocalDateTime.now());
        log.info("Deleted refresh tokens: {}", deleted);
    }

}
