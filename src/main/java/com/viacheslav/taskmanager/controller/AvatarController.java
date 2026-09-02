package com.viacheslav.taskmanager.controller;

import com.viacheslav.taskmanager.model.dto.AvatarResponse;
import com.viacheslav.taskmanager.model.dto.AvatarUploadResult;
import com.viacheslav.taskmanager.security.model.CustomUserDetails;
import com.viacheslav.taskmanager.service.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/v1/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping
    public ResponseEntity<AvatarResponse> uploadAvatar(@RequestParam("file") MultipartFile file,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userAccountId = userDetails.getUserId();
        AvatarUploadResult uploadResult = avatarService.uploadAvatar(userAccountId, file);
        return ResponseEntity.ok(AvatarResponse.builder()
                .avatarUrl(uploadResult.avatarUrl())
                .avatarKey(uploadResult.avatarKey())
                .build());
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAvatar(@AuthenticationPrincipal CustomUserDetails userDetails) {
        avatarService.deleteAvatar(userDetails.getUserId());
    }

    @GetMapping
    public ResponseEntity<String> getAvatar(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String url = avatarService.getAvatarUrl(userDetails.getUserId());
        return ResponseEntity.ok(url);
    }
}
