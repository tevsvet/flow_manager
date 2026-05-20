package com.program.flow_manager.api.dto;

import com.program.flow_manager.domain.model.ConversionStatus;

import java.util.UUID;

public record ConversionSubmitResponse(
        UUID taskId,
        ConversionStatus status
) { }

