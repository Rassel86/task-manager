package com.viacheslav.taskmanager.service.impl;

import com.viacheslav.taskmanager.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Override
    public String saveFile(String bucket, String objectKey, MultipartFile file) {

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request, RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            log.info("Avatar uploader: {}", objectKey);
        } catch (IOException e) {
            log.error("Failed to upload avatar: {}", e.getMessage());
            throw new RuntimeException("Failed to upload avatar", e);
        }

        return getTemporaryUrl(bucket, objectKey, Duration.ofDays(7));
    }

    @Override
    public void deleteFile(String bucket, String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            return;
        }

        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(request);
            log.info("Object deleted: {}", objectKey);
        } catch (Exception e) {
            log.warn("Failed to delete avatar: {}", e.getMessage());
        }
    }

    @Override
    public String getTemporaryUrl(String bucket, String objectKey, Duration duration) {
        if (objectKey == null || objectKey.isEmpty()) {
            return null;
        }

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(duration)
                .getObjectRequest(request)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }
}
