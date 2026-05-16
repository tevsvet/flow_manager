package com.program.flow_manager.api.controller;

import com.program.flow_manager.api.dto.ConversionStatusResponse;
import com.program.flow_manager.api.dto.ConversionSubmitResponse;
import com.program.flow_manager.api.mapper.ConversionResponseMapper;
import com.program.flow_manager.domain.model.ConversionTaskEntity;
import com.program.flow_manager.service.task.ConversionQueryService;
import com.program.flow_manager.service.task.ConversionFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Tag(name = "Conversions", description = "API for uploading files, tracking conversion state and downloading results")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/conversions")
public class ConversionController {

    private final ConversionFacade conversionFacade;
    private final ConversionQueryService conversionQueryService;
    private final ConversionResponseMapper conversionResponseMapper;

    @Operation(summary = "Upload a file and submit it for PDF conversion")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "202",
                    description = "Conversion task accepted",
                    content = @Content(schema = @Schema(implementation = ConversionSubmitResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid multipart request"),
            @ApiResponse(responseCode = "502", description = "Source file could not be stored in MinIO"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ConversionSubmitResponse> submit(@RequestPart("file") MultipartFile file) {
        ConversionTaskEntity task = conversionFacade.submit(file);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(conversionResponseMapper.toSubmitResponse(task));
    }

    @Operation(summary = "Get conversion status by task ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Conversion task found",
                    content = @Content(schema = @Schema(implementation = ConversionStatusResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Conversion task not found")
    })
    @GetMapping("/{taskId}")
    public ConversionStatusResponse getStatus(@PathVariable UUID taskId) {
        return conversionResponseMapper.toStatusResponse(conversionQueryService.getById(taskId));
    }

    @Operation(summary = "Download converted PDF file by task ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Converted file downloaded"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "409", description = "Converted file is not ready yet")
    })
    @GetMapping("/{taskId}/file")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID taskId) {
        InputStream inputStream = conversionQueryService.downloadResult(taskId);
        ConversionTaskEntity task = conversionQueryService.getById(taskId);
        String fileName = task.getId() + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new InputStreamResource(inputStream));
    }
}
