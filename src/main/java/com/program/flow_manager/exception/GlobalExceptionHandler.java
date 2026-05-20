package com.program.flow_manager.exception;

import com.program.flow_manager.api.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ConversionTaskNotFoundException.class})
    public ResponseEntity<ApiError> handleNotFound(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "CONVERSION_TASK_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler({FileNotReadyException.class})
    public ResponseEntity<ApiError> handleFileNotReady(RuntimeException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, "FILE_NOT_READY", ex.getMessage(), request);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ApiError> handleMissingRequestPart(MissingServletRequestPartException ex, HttpServletRequest request) {
        String message = "Required request part is missing: " + ex.getRequestPartName();
        return buildResponse(HttpStatus.BAD_REQUEST, "MISSING_REQUEST_PART", message, request);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiError> handleMultipart(MultipartException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "MULTIPART_ERROR", ex.getMessage(), request);
    }

    @ExceptionHandler({UnsupportedFileTypeException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ApiError> handleStorage(StorageException ex, HttpServletRequest request) {
        log.error("Storage operation failed", ex);
        return buildResponse(HttpStatus.BAD_GATEWAY, "STORAGE_ERROR", ex.getMessage(), request);
    }

    @ExceptionHandler(ConversionSubmissionException.class)
    public ResponseEntity<ApiError> handleConversionSubmission(ConversionSubmissionException ex, HttpServletRequest request) {
        log.error("Conversion submission failed", ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "CONVERSION_SUBMISSION_FAILED",
                "Failed to submit file for conversion",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("Unexpected server error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Unexpected server error", request);
    }

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request
    ) {
        return ResponseEntity.status(status).body(
                new ApiError(
                        code,
                        message,
                        request.getRequestURI(),
                        Instant.now()
                )
        );
    }
}
