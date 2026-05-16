package com.program.flow_manager.service.task;

import com.program.flow_manager.domain.dao.ConversionTaskRepository;
import com.program.flow_manager.domain.model.ConversionStatus;
import com.program.flow_manager.domain.model.ConversionTaskEntity;
import com.program.flow_manager.exception.ConversionTaskNotFoundException;
import com.program.flow_manager.exception.FileNotReadyException;
import com.program.flow_manager.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversionQueryService {

    private final ConversionTaskRepository conversionTaskRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public ConversionTaskEntity getById(UUID taskId) {
        return conversionTaskRepository.findById(taskId)
                .orElseThrow(() -> new ConversionTaskNotFoundException("Conversion task not found: " + taskId));
    }

    @Transactional(readOnly = true)
    public InputStream downloadResult(UUID taskId) {
        ConversionTaskEntity task = getById(taskId);
        if (task.getStatus() != ConversionStatus.SUCCESS || task.getResultBucket() == null || task.getResultObjectKey() == null) {
            throw new FileNotReadyException("Converted file is not ready for task: " + taskId);
        }
        return fileStorageService.download(task.getResultBucket(), task.getResultObjectKey());
    }
}
