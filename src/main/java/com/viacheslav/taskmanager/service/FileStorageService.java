package com.viacheslav.taskmanager.service;

import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;

public interface FileStorageService {
    String saveFile(String bucket, String objectKey, MultipartFile file);
    void deleteFile(String bucket, String objectKey);
    String getTemporaryUrl(String bucket, String objectKey, Duration duration);
}
