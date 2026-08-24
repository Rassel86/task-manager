package com.viacheslav.taskmanager.security.service;

import com.viacheslav.taskmanager.model.UserAccount;

public interface RefreshTokenService {
    String generateRefreshToken(UserAccount user);
    void revokeRefreshToken(String rawToken);
    boolean isRefreshTokenValid(String rawToken);
    UserAccount getUserByRefreshToken(String rawToken);
}
