package com.viacheslav.taskmanager.controller;

import com.viacheslav.taskmanager.dto.PageResponse;
import com.viacheslav.taskmanager.dto.auth.SuccessResponse;
import com.viacheslav.taskmanager.dto.user.ResetPasswordRequest;
import com.viacheslav.taskmanager.dto.user.UserFilterRequest;
import com.viacheslav.taskmanager.dto.user.UserResponse;
import com.viacheslav.taskmanager.dto.user.UserUpdateByAdminRequest;
import com.viacheslav.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> getUsersPage(@ModelAttribute UserFilterRequest filter) {
        PageResponse<UserResponse> response = userService.getUsersPage(filter);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchUser() {
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") UUID userId,
                                                   @Valid @RequestBody UserUpdateByAdminRequest request) {
        UserResponse response = userService.updateUserByAdmin(userId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<SuccessResponse> blockUser(@PathVariable("id") UUID userId) {
        userService.blockUser(userId);
        return ResponseEntity.ok(SuccessResponse.builder()
                .message(String.format("User with id %s blocked", userId))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/{id}/unblock")
    public ResponseEntity<SuccessResponse> unblockUser(@PathVariable("id") UUID userId) {
        userService.unblockUser(userId);
        return ResponseEntity.ok(SuccessResponse.builder()
                .message(String.format("User with id %s unblocked", userId))
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<SuccessResponse> resetPassword(@PathVariable("id") UUID userId,
                                                         @Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPasswordByAdmin(userId, request.newPassword());
        return ResponseEntity.ok(SuccessResponse.builder()
                .message("Password reset successfully")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
