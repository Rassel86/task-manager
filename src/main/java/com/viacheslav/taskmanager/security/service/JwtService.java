package com.viacheslav.taskmanager.security.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    String generateAccessToken(Authentication authentication);
    String generateRefreshToken(UserDetails userDetails);
    String generateTokenWithClaims(UserDetails userDetails, Map<String, Object> extraClaims);

    boolean isTokenValid(String token);
    boolean isTokenExpired(String token);

    String extractUsername(String token);
    List<GrantedAuthority> getAuthorities(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    Date getExpirationDate(String token);

    Authentication getAuthentication(String token);
}
