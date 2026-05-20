package com.program.flow_manager.kafka.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConversionRequestEvent(
        UUID taskId,
        String bucket,
        String sourceObjectKey,
        String fileType
) { }
