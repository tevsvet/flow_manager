package com.program.flow_manager.service.task;

import com.program.flow_manager.domain.dao.ConversionTaskRepository;
import com.program.flow_manager.domain.model.ConversionStatus;
import com.program.flow_manager.domain.model.ConversionTaskEntity;
import com.program.flow_manager.domain.model.SupportedFileType;
import com.program.flow_manager.kafka.dto.ConversionRequestEvent;
import com.program.flow_manager.service.outbox.OutboxService;
import com.program.flow_manager.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversionFacade {

    private final ConversionTaskRepository conversionTaskRepository;
    private final FileStorageService fileStorageService;
    private final OutboxService outboxService;

    @Transactional
    public ConversionTaskEntity submit(MultipartFile file) {
        UUID taskId = UUID.randomUUID();
        SupportedFileType fileType = SupportedFileType.fromFilename(file.getOriginalFilename()).normalized();
        FileStorageService.StoredObject storedObject = fileStorageService.storeSourceFile(taskId, file, fileType);

        ConversionTaskEntity task = ConversionTaskEntity.builder()
                .id(taskId)
                .status(ConversionStatus.IN_PROGRESS)
                .sourceBucket(storedObject.bucket())
                .sourceObjectKey(storedObject.objectKey())
                .sourceFileType(fileType)
                .build();

        conversionTaskRepository.save(task);
        outboxService.enqueue(
                "CONVERSION_TASK",
                taskId,
                "conversion-request:" + taskId,
                taskId.toString(),
                new ConversionRequestEvent(
                        taskId,
                        storedObject.bucket(),
                        storedObject.objectKey(),
                        fileType.name()
                )
        );
        return task;
    }
}
