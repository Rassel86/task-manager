package com.viacheslav.taskmanager.security.service;

import com.viacheslav.taskmanager.model.UserAccount;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    String generateAccessToken(UserAccount account);

    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);

    String extractLogin(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

}
