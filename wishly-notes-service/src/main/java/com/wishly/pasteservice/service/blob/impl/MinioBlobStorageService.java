package com.wishly.pasteservice.service.blob.impl;

import com.wishly.pasteservice.exception.BlobStorageException;
import com.wishly.pasteservice.service.blob.BlobStorageService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioBlobStorageService implements BlobStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name:pastes}")
    private String bucketName;

    private static final String CONTENT_TYPE = "text/plain";

    @Override
    public void store(String key, String content) {
        try {
            ensureBucketExists();

            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(contentBytes)) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(key)
                                .stream(inputStream, contentBytes.length, -1)
                                .contentType(CONTENT_TYPE)
                                .build()
                );
                log.debug("Stored blob: {}", key);
            }
        } catch (Exception e) {
            log.error("Failed to store blob {}: {}", key, e.getMessage());
            throw new BlobStorageException("Failed to store content", e);
        }
    }

    @Override
    public String retrieve(String key) {
        try {
            log.debug("Retrieving blob: {}", key);

            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );

            return new String(response.readAllBytes(), StandardCharsets.UTF_8);

        } catch (Exception e) {
            log.error("Failed to retrieve blob {}: {}", key, e.getMessage());
            throw new BlobStorageException("Failed to retrieve content", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(key)
                            .build()
            );
            log.debug("Deleted blob: {}", key);
        } catch (Exception e) {
            log.error("Failed to delete blob {}: {}", key, e.getMessage());
        }
    }

    private void ensureBucketExists() throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
            );
            log.info("Created bucket: {}", bucketName);
        }
    }
}
