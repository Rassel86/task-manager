package com.viacheslav.taskmanager.controller;

import com.viacheslav.taskmanager.model.dto.auth.ChangePasswordRequest;
import com.viacheslav.taskmanager.model.dto.auth.SuccessResponse;
import com.viacheslav.taskmanager.model.dto.user.UserResponse;
import com.viacheslav.taskmanager.model.dto.user.UserUpdateRequest;
import com.viacheslav.taskmanager.security.model.CurrentUser;
import com.viacheslav.taskmanager.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5173"})
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserAccountService userAccountService;

    @GetMapping
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal CurrentUser currentUser) {
        UserResponse response = userAccountService.getByEmail(currentUser.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<SuccessResponse> changePassword(@AuthenticationPrincipal CurrentUser currentUser,
                                                          @Valid @RequestBody ChangePasswordRequest request) {
        userAccountService.changePassword(currentUser.getCredentials().getUserAccount(), request);
        return ResponseEntity.ok(SuccessResponse.builder()
                .message("Password changed successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal CurrentUser currentUser,
                                                      @Valid @RequestBody UserUpdateRequest request) {
        UserResponse response = userAccountService.updateUser(currentUser.getCredentials().getUserAccount(), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfile(@AuthenticationPrincipal CurrentUser currentUser) {
        UUID userId = currentUser.getId();
        userAccountService.deleteUser(userId);
    }
}
