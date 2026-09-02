package com.viacheslav.taskmanager.service;

import com.viacheslav.taskmanager.model.dto.AvatarUploadResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface AvatarService {
    AvatarUploadResult uploadAvatar(UUID userAccountId, MultipartFile file);
    void deleteAvatar(UUID userAccountId);
    String getAvatarUrl(UUID userAccountId);
}
