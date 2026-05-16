package com.program.flow_manager.service.storage;

import com.program.flow_manager.domain.model.SupportedFileType;
import com.program.flow_manager.exception.StorageException;
import com.program.flow_manager.minio.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MinioService minioService;

    public StoredObject storeSourceFile(UUID taskId, MultipartFile file, SupportedFileType fileType) {
        try {
            String bucket = minioService.defaultBucket();
            String extension = fileType.name().toLowerCase();
            String objectKey = "incoming/" + taskId + "/source." + extension;
            minioService.upload(
                    bucket,
                    objectKey,
                    file.getBytes(),
                    file.getSize(),
                    resolveContentType(file)
            );
            return new StoredObject(bucket, objectKey);
        } catch (IOException ex) {
            throw new StorageException("Failed to read uploaded file", ex);
        }
    }

    public InputStream download(String bucket, String objectKey) {
        return minioService.download(bucket, objectKey);
    }

    private String resolveContentType(MultipartFile file) {
        return file.getContentType() != null ? file.getContentType() : "application/octet-stream";
    }

    public record StoredObject(String bucket, String objectKey) { }
}
