package com.viacheslav.taskmanager.controller;

import com.viacheslav.taskmanager.model.dto.PageResponse;
import com.viacheslav.taskmanager.model.dto.auth.SuccessResponse;
import com.viacheslav.taskmanager.model.dto.user.ResetPasswordRequest;
import com.viacheslav.taskmanager.model.dto.user.UserFilterRequest;
import com.viacheslav.taskmanager.model.dto.user.UserResponse;
import com.viacheslav.taskmanager.model.dto.user.UserUpdateByAdminRequest;
import com.viacheslav.taskmanager.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserAccountService userAccountService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") UUID userId) {
        UserResponse userResponse = userAccountService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getUsersPage(@ModelAttribute UserFilterRequest filter) {
        PageResponse<UserResponse> response = userAccountService.getUsersPage(filter);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") UUID userId,
                                                   @Valid @RequestBody UserUpdateByAdminRequest request) {
        UserResponse response = userAccountService.updateUserByAdmin(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<SuccessResponse> blockUser(@PathVariable("id") UUID userId) {
        userAccountService.blockUser(userId);
        return ResponseEntity.ok(SuccessResponse.builder()
                .message(String.format("UserAccount with id %s blocked", userId))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<SuccessResponse> unblockUser(@PathVariable("id") UUID userId) {
        userAccountService.unblockUser(userId);
        return ResponseEntity.ok(SuccessResponse.builder()
                .message(String.format("UserAccount with id %s unblocked", userId))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<SuccessResponse> resetPassword(@PathVariable("id") UUID userId,
                                                         @Valid @RequestBody ResetPasswordRequest request) {
        userAccountService.resetPasswordByAdmin(userId, request.newPassword());
        return ResponseEntity.ok(SuccessResponse.builder()
                .message("Password reset successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") UUID userId) {
        userAccountService.deleteUserByAdmin(userId);
    }
}
