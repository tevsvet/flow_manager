package com.program.flow_manager.api.dto;

import java.time.Instant;

public record ApiError(
        String code,
        String message,
        String path,
        Instant timestamp
) { }

