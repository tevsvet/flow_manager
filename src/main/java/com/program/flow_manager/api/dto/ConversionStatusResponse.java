package com.program.flow_manager.api.dto;

import com.program.flow_manager.domain.model.ConversionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConversionStatusResponse(
        UUID taskId,
        ConversionStatus status,
        String sourceBucket,
        String sourceObjectKey,
        String resultBucket,
        String resultObjectKey,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }

