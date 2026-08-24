package com.viacheslav.taskmanager.security.repository;

import com.viacheslav.taskmanager.security.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String token);

    int deleteAllByExpiresAtBeforeOrRevokedIsTrue(LocalDateTime now);
}
