package com.program.flow_manager.minio;

import com.program.flow_manager.config.properties.MinioProperties;
import com.program.flow_manager.exception.StorageException;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public void ensureBucketExists() {
        String bucket = minioProperties.defaultBucket();
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception ex) {
            throw new StorageException("Failed to initialize bucket: " + bucket, ex);
        }
    }

    public String resolveBucket(String requestedBucket) {
        return StringUtils.hasText(requestedBucket) ? requestedBucket : minioProperties.defaultBucket();
    }

    public String defaultBucket() {
        return minioProperties.defaultBucket();
    }

    public InputStream download(String bucket, String objectKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception ex) {
            throw new StorageException("Failed to download object '" + objectKey + "' from bucket '" + bucket + "'", ex);
        }
    }

    public void upload(String bucket, String objectKey, InputStream inputStream, long size, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(inputStream, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception ex) {
            throw new StorageException("Failed to upload object '" + objectKey + "' to bucket '" + bucket + "'", ex);
        }
    }

    public void delete(String bucket, String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception ex) {
            throw new StorageException("Failed to delete object '" + objectKey + "' from bucket '" + bucket + "'", ex);
        }
    }
}
