package com.program.flow_manager.service.task;

import com.program.flow_manager.domain.model.ConversionTaskEntity;
import com.program.flow_manager.domain.model.SupportedFileType;
import com.program.flow_manager.exception.ConversionSubmissionException;
import com.program.flow_manager.exception.StorageException;
import com.program.flow_manager.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversionFacade {

    private final ConversionTaskPersistenceService persistenceService;
    private final FileStorageService fileStorageService;

    public ConversionTaskEntity submit(MultipartFile file) {
        UUID taskId = UUID.randomUUID();
        SupportedFileType fileType = SupportedFileType.fromFilename(file.getOriginalFilename()).normalized();
        FileStorageService.StoredObject storedObject = fileStorageService.storeSourceFile(taskId, file, fileType);

        try {
            return persistenceService.createTaskAndRegisterOutbox(taskId, fileType, storedObject);
        } catch (RuntimeException ex) {
            try {
                fileStorageService.delete(storedObject.bucket(), storedObject.objectKey());
            } catch (StorageException deleteEx) {
                ex.addSuppressed(deleteEx);
            }
            throw new ConversionSubmissionException(
                    "Failed to persist conversion task after file " + file.getOriginalFilename() + " upload",
                    ex
            );
        }
    }
}
