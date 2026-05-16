package com.program.flow_manager.exception;

import org.jetbrains.annotations.NotNull;

public class ConversionTaskNotFoundException extends RuntimeException {
    public ConversionTaskNotFoundException(String message) {
        super(message);
    }
}
