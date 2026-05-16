package com.program.flow_manager.kafka.dto;

import com.program.flow_manager.domain.model.ConversionStatus;

import java.util.UUID;

public record ConversionResultEvent(
        UUID taskId,
        String sourceBucket,
        String sourceObjectKey,
        String resultBucket,
        String resultObjectKey,
        ConversionStatus status,
        String errorMessage

) { }
