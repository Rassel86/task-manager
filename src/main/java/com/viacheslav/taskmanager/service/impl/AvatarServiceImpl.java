package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.exception.ResourceNotFoundException;
import com.viacheslav.taskmanager.model.UserAccount;
import com.viacheslav.taskmanager.model.dto.AvatarUploadResult;
import com.viacheslav.taskmanager.repository.UserAccountRepository;
import com.viacheslav.taskmanager.service.AvatarService;
import com.viacheslav.taskmanager.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private final UserAccountRepository userAccountRepository;
    private final FileStorageService fileStorageService;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final String AVATAR_BUCKET = "avatars";

    @Override
    @Transactional
    public AvatarUploadResult uploadAvatar(UUID userAccountId, MultipartFile file) {
        validateFile(file);

        UserAccount userAccount = getUserAccountEntityById(userAccountId);

        if (userAccount.getAvatarKey() != null) {
            fileStorageService.deleteFile(AVATAR_BUCKET, userAccount.getAvatarKey());
        }

        String objectKey = generateAvatarKey(userAccountId, file.getOriginalFilename());
        String url = fileStorageService.saveFile(AVATAR_BUCKET, objectKey, file);

        userAccount.setAvatarKey(objectKey);
        userAccountRepository.save(userAccount);
        return AvatarUploadResult.builder()
                .avatarKey(objectKey)
                .avatarUrl(url)
                .build();
    }

    @Override
    @Transactional
    public void deleteAvatar(UUID userAccountId) {
        UserAccount account = getUserAccountEntityById(userAccountId);

        if (account.getAvatarKey() != null) {
            fileStorageService.deleteFile(AVATAR_BUCKET, account.getAvatarKey());
        }
    }

    @Override
    public String getAvatarUrl(UUID userAccountId) {
        UserAccount account = getUserAccountEntityById(userAccountId);
        return fileStorageService.getTemporaryUrl(AVATAR_BUCKET, account.getAvatarKey(), Duration.ofDays(7));
    }

    private UserAccount getUserAccountEntityById(UUID userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("UserAccount with ID %s not found", userId)));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        String contentType = file.getContentType();
        List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/gif", "image/webp");

        if (contentType == null || contentType.isEmpty() || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("Only images are allowed: JPEG, PNG, GIF, WEBP");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size must be less than " + MAX_FILE_SIZE / (1024 * 1024) + "MB");
        }
    }

    private String generateAvatarKey(UUID userAccountId, String fileName) {
        return String.format("%s/%s.%s",
                userAccountId,
                UUID.randomUUID(),
                getFileExtension(fileName));
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "jpg";
        }
        int lastDot = filename.lastIndexOf(".");
        return lastDot > 0 ? filename.substring(lastDot + 1).toLowerCase() : "jpg";
    }
}
