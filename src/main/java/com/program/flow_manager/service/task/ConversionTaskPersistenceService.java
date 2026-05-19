package com.program.flow_manager.service.task;

import com.program.flow_manager.domain.dao.ConversionTaskRepository;
import com.program.flow_manager.domain.model.ConversionTaskEntity;
import com.program.flow_manager.domain.model.SupportedFileType;
import com.program.flow_manager.kafka.dto.ConversionRequestEvent;
import com.program.flow_manager.service.outbox.OutboxService;
import com.program.flow_manager.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.program.flow_manager.domain.model.ConversionStatus.IN_PROGRESS;
import static com.program.flow_manager.domain.model.OutboxAggregateType.CONVERSION_TASK;

@Service
@RequiredArgsConstructor
public class ConversionTaskPersistenceService {

    private final ConversionTaskRepository conversionTaskRepository;
    private final OutboxService outboxService;

    @Transactional
    public ConversionTaskEntity createTaskAndRegisterOutbox(UUID taskId,
                                               SupportedFileType fileType,
                                               FileStorageService.StoredObject storedObject) {
        ConversionTaskEntity task = ConversionTaskEntity.builder()
                .id(taskId)
                .status(IN_PROGRESS)
                .sourceBucket(storedObject.bucket())
                .sourceObjectKey(storedObject.objectKey())
                .sourceFileType(fileType)
                .build();

        conversionTaskRepository.save(task);
        outboxService.enqueue(
                CONVERSION_TASK,
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
